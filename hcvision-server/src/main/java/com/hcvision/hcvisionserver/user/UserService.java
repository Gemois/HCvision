package com.hcvision.hcvisionserver.user;

import com.hcvision.hcvisionserver.auth.token.ConfirmationToken;
import com.hcvision.hcvisionserver.auth.token.ConfirmationTokenService;
import com.hcvision.hcvisionserver.auth.token.dto.TokenType;
import com.hcvision.hcvisionserver.config.JwtService;
import com.hcvision.hcvisionserver.mail.EmailService;
import com.hcvision.hcvisionserver.user.dto.ForgotPasswordRequest;
import com.hcvision.hcvisionserver.user.dto.ResetPasswordRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

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


}
