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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



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

    private static final SecureRandom secureRandom = new SecureRandom();

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(resetPasswordRequest.getToken());
        userRepository.changePassword(confirmationToken.getUser().getEmail(), passwordEncoder.encode(resetPasswordRequest.getPassword()));
        emailService.send(confirmationToken.getUser().getEmail(),
                emailService.buildPasswordChangedEmail(confirmationToken.getUser().getFirstName()),
                EmailService.PASSWORD_CHANGE_NOTIFICATION_SUBJECT);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> _user = userRepository.findByEmail(request.getEmail());
        if (_user.isPresent()) {
            User user = _user.get();
            String otp = generateOTP();
            ConfirmationToken confirmationToken = new ConfirmationToken(otp, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), user);
            confirmationToken.setType(TokenType.OTP);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            emailService.send(request.getEmail(),
                    emailService.buildOtpEmail(user.getFirstName(), otp),
                    EmailService.RESET_PASSWORD_OTP_SUBJECT);
        }
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
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }


    public void deleteUser(String jwt) {
        User user = getUserFromJwt(jwt);

        List<Optimal> userOptimalResults = optimalRepository.findByUser(user);
        userOptimalResults.forEach(optimal -> {
            File optimalDir = new File(HierarchicalService.getBaseResultPathByPythonScript(optimal));
            if (optimalDir.exists())
                DatasetUtils.deleteUserDirectory(optimalDir);
        });
        optimalRepository.deleteAllUserOptimal(user);

        List<Analysis> userAnalysisResults = analysisRepository.findByUser(user);
        userAnalysisResults.forEach(analysis -> {
            File analysisDir = new File(HierarchicalService.getBaseResultPathByPythonScript(analysis));
            if (analysisDir.exists())
                DatasetUtils.deleteUserDirectory(analysisDir);
        });
        analysisRepository.deleteAllUserAnalysis(user);

        List<Dataset> userPublicDatasetList = datasetRepository.findByUserAndAccessType(user, AccessType.PUBLIC);
        userPublicDatasetList.forEach(dataset -> {
            File publicDataset = new File(dataset.getPath());
            if (publicDataset.exists())
                DatasetUtils.deleteUserDirectory(publicDataset);
        });
        datasetRepository.deleteAllUserDatasets(user);

        File userDir = new File(DatasetService.getUserDirectoryPathByType(AccessType.PRIVATE, user));
        if (userDir.exists())
            DatasetUtils.deleteUserDirectory(userDir);

        confirmationTokenRepository.deleteAllUserConfirmationTokens(user);

        userRepository.deleteById(user.getId());
    }

    public void updateUser(EditUserRequest editUserRequest, String jwt) {
        boolean emailChanged = false;
        User user = getUserFromJwt(jwt);

        if (!editUserRequest.getNewEmail().equals(user.getEmail())) {
            user.setEmail(editUserRequest.getNewEmail());
            user.setActivated(false);
            emailChanged = true;
        }

        if (!editUserRequest.getNewFirstName().equals(user.getFirstName())) {
            user.setFirstName(editUserRequest.getNewFirstName());
        }

        if (!editUserRequest.getNewLastName().equals(user.getLastName())) {
            user.setLastName(editUserRequest.getNewLastName());
        }

        if (!passwordEncoder.encode(editUserRequest.getNewPassword()).equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(editUserRequest.getNewPassword()));
        }
        userRepository.save(user);
        if (emailChanged) {
            String token = confirmationTokenService.createConfirmationToken(user);

            try {
                String link = "http://localhost:8080/api/v1/auth/confirm?token=" + token;
                emailService.send(user.getEmail(),
                        emailService.buildVerificationEmail(user.getFirstName(), link),
                        EmailService.EMAIL_VERIFICATION_SUBJECT);
            } catch (Exception ignored) { }

        }
    }
}
