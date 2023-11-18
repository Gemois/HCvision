package com.hcvision.hcvisionserver.user;

import com.hcvision.hcvisionserver.auth.token.ConfirmationToken;
import com.hcvision.hcvisionserver.auth.token.ConfirmationTokenRepository;
import com.hcvision.hcvisionserver.auth.token.ConfirmationTokenService;
import com.hcvision.hcvisionserver.auth.token.dto.TokenType;
import com.hcvision.hcvisionserver.config.JwtService;
import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.dataset.DatasetRepository;
import com.hcvision.hcvisionserver.dataset.DatasetService;
import com.hcvision.hcvisionserver.dataset.DatasetUtils;
import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.hierarchical.HierarchicalService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalRepository;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisRepository;
import com.hcvision.hcvisionserver.mail.EmailService;
import com.hcvision.hcvisionserver.user.dto.EditUserRequest;
import com.hcvision.hcvisionserver.user.dto.ForgotPasswordRequest;
import com.hcvision.hcvisionserver.user.dto.ResetPasswordRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final DatasetRepository datasetRepository;
    private final OptimalRepository optimalRepository;
    private final AnalysisRepository analysisRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public User.ProjectUser getUserByEmail(String jwt) {
        String email = jwtService.extractUsername(jwt.substring(7));
        return userRepository.getUserByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public void enableUser(String email) {
        userRepository.enableUser(email);
    }


    public String resetPassword(ResetPasswordRequest request) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("token not valid"));

        if (!confirmationToken.getType().equals(TokenType.OTP))
            throw new BadRequestException("token not valid");

        if (confirmationToken.getConfirmedAt() != null)
            throw new BadRequestException("email already confirmed");

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("token expired");
        }

        confirmationTokenService.setConfirmedAt(request.getToken());
        userRepository.changePassword(confirmationToken.getUser().getEmail(), passwordEncoder.encode(request.getPassword()));
        try {
            emailService.send(confirmationToken.getUser().getEmail(),
                    emailService.buildPasswordChangedEmail(confirmationToken.getUser().getFirstName()),
                    EmailService.PASSWORD_CHANGE_NOTIFICATION_SUBJECT);
        } catch (Exception e) {
            logger.warn("Failed to send changed password notification to user {}: {}", confirmationToken.getUser(), e.getMessage());
        }
        logger.info("User had his password reset : userId: " + confirmationToken.getUser().getId() + ", email: " + confirmationToken.getUser().getEmail());
        return msg("Password was reset successfully");
    }


    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String otp = generateOTP();
        ConfirmationToken confirmationToken = new ConfirmationToken(otp, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), user);
        confirmationToken.setType(TokenType.OTP);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        emailService.send(request.getEmail(),
                emailService.buildOtpEmail(user.getFirstName(), otp),
                EmailService.RESET_PASSWORD_OTP_SUBJECT);

        logger.info("Forgot password request processed successfully - Email: {}", request.getEmail());
        return msg("Email with password reset token has been send");
    }


    private static final String CHARACTERS = "0123456789";


    public static String generateOTP() {
        StringBuilder token = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(randomIndex));
        }
        return token.toString();
    }


    public User getUserFromJwt(String jwt) {
        return userRepository.findByEmail(jwtService.extractUsername(jwt.substring(7)))
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    public String deleteUser(String jwt) {
        User user = getUserFromJwt(jwt);

        try {
            List<Optimal> userOptimalResults = optimalRepository.findByUser(user);
            userOptimalResults.forEach(optimal -> {
                File optimalDir = new File(HierarchicalService.getBaseResultPathByPythonScript(optimal));
                if (optimalDir.exists()) DatasetUtils.deleteAllRecursively(optimalDir);
            });
            optimalRepository.deleteAllUserOptimal(user);

            List<Analysis> userAnalysisResults = analysisRepository.findByUser(user);
            userAnalysisResults.forEach(analysis -> {
                File analysisDir = new File(HierarchicalService.getBaseResultPathByPythonScript(analysis));
                if (analysisDir.exists()) DatasetUtils.deleteAllRecursively(analysisDir);
            });
            analysisRepository.deleteAllUserAnalysis(user);

            List<Dataset> userPublicDatasetList = datasetRepository.findByUserAndAccessType(user, AccessType.PUBLIC);
            userPublicDatasetList.forEach(dataset -> {
                File publicDataset = new File(dataset.getPath());
                if (publicDataset.exists()) DatasetUtils.deleteAllRecursively(publicDataset);
            });
            datasetRepository.deleteAllUserDatasets(user);

            File userDir = new File(DatasetService.getUserDirectoryPathByType(AccessType.PRIVATE, user));
            if (userDir.exists()) DatasetUtils.deleteAllRecursively(userDir);

            confirmationTokenRepository.deleteAllUserConfirmationTokens(user);
            userRepository.deleteById(user.getId());
        } catch (Exception e) {
            logger.error("Error deleting user - UserId: {}, Email: {}. Error: {}", user.getId(), user.getEmail(), e.getMessage());
        }

        logger.info("User deleted successfully - UserId: {}, Email: {}", user.getId(), user.getEmail());
        return msg("User deleted along with all his information.");
    }


    public String updateUserDetails(EditUserRequest request, String jwt) {
        boolean emailChanged = false;
        User user = getUserFromJwt(jwt);

        if (!request.getNewEmail().equals(user.getEmail())) {
            user.setEmail(request.getNewEmail());
            user.setActivated(false);
            emailChanged = true;
        }

        if (!request.getNewFirstName().equals(user.getFirstName()))
            user.setFirstName(request.getNewFirstName());

        if (!request.getNewLastName().equals(user.getLastName()))
            user.setLastName(request.getNewLastName());

        if (!passwordEncoder.encode(request.getNewPassword()).equals(user.getPassword()))
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        if (emailChanged) {
            String token = confirmationTokenService.createConfirmationToken(user);

            try {
                String link = "http://localhost:8080/api/v1/auth/confirm?token=" + token;
                emailService.send(user.getEmail(),
                        emailService.buildVerificationEmail(user.getFirstName(), link),
                        EmailService.EMAIL_VERIFICATION_SUBJECT);
                logger.info("Verification email sent after successful email change - UserId: {}, Email: {}", user.getId(), user.getEmail());
            } catch (Exception e) {
                logger.warn("Could not send verification email after successful email change - UserId: {}, Email: {}. Error: {}", user.getId(), user.getEmail(), e.getMessage());
            }

        }

        logger.info("User profile updated successfully - UserId: {}, Email: {}", user.getId(), user.getEmail());
        return msg("User profile updated successfully.");
    }


    public String msg(String msg) {
        return "{\"success_msg\": \"" + msg + "\"}";
    }
}
