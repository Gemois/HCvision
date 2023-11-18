package com.hcvision.hcvisionserver.auth;


import com.hcvision.hcvisionserver.auth.dto.AuthenticationRequest;
import com.hcvision.hcvisionserver.auth.dto.AuthenticationResponse;
import com.hcvision.hcvisionserver.auth.dto.RegisterRequest;
import com.hcvision.hcvisionserver.auth.dto.RegisterResponse;
import com.hcvision.hcvisionserver.auth.token.ConfirmationToken;
import com.hcvision.hcvisionserver.auth.token.dto.ConfirmationTokenResponse;
import com.hcvision.hcvisionserver.auth.token.ConfirmationTokenService;
import com.hcvision.hcvisionserver.auth.token.dto.TokenType;
import com.hcvision.hcvisionserver.config.JwtService;
import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.mail.EmailService;
import com.hcvision.hcvisionserver.mail.EmailValidator;
import com.hcvision.hcvisionserver.user.dto.Role;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserRepository;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Authentication failed user - username: {}", request.getEmail());
                    return new NotFoundException("User not found");
                });

        String jwtToken = jwtService.generateToken(user);
        logger.info("User authenticated successfully - username: {}", user.getUsername());
        return AuthenticationResponse.builder()
                .name(user.getFirstName())
                .email(user.getEmail())
                .confirmed(user.isActivated())
                .role(user.getRole()).accessToken(jwtToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration()))
                .build();
    }


    public RegisterResponse register(RegisterRequest request) {
        boolean userExists = userRepository.findByEmail(request.getEmail()).isPresent();

        if (userExists)
            throw new BadRequestException("email already taken");

        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail)
            throw new BadRequestException("email not valid");

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = confirmationTokenService.createConfirmationToken(user);

        try {
            String link = "http://localhost:8080/api/v1/auth/confirm?token=" + token;
            emailService.send(request.getEmail(), emailService.buildVerificationEmail(request.getFirstname(), link),
                    EmailService.EMAIL_VERIFICATION_SUBJECT);
        } catch (Exception e) {
            logger.warn("Failed to send verification email to user {}: {}", user.getUsername(), e.getMessage());
        }

        logger.info("User registered - username: {}, email: {}", user.getUsername(), user.getEmail());
        return RegisterResponse.builder()
                .name(user.getFirstName())
                .email(user.getEmail())
                .role(user.getRole())
                .confirmed(user.isActivated())
                .registeredAt(LocalDateTime.now())
                .build();
    }


    public ConfirmationTokenResponse confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new BadRequestException("token not valid"));

        if (!confirmationToken.getType().equals(TokenType.UUID))
            throw new BadRequestException("token is not valid");

        if (confirmationToken.getConfirmedAt() != null)
            throw new BadRequestException("email already confirmed");

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now()))
            throw new BadRequestException("token expired");

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());
        logger.info("User {} has been successfully activated.", confirmationToken.getUser().getUsername());
        return ConfirmationTokenResponse.builder()
                .confirmed(true)
                .confirmedAt(LocalDateTime.now())
                .msg("Email confirmed").build();
    }


    public String sendConfirmationEmail(String jwt) {
        User user = userRepository.findByEmail(jwtService.extractUsername(jwt.substring(7)))
                .orElseThrow(() -> new NotFoundException("User does not exist"));

        if (user.isActivated())
            throw new BadRequestException("user is already activated!");

        confirmationTokenService.retireTokens(user);
        String token = confirmationTokenService.createConfirmationToken(user);

        logger.info("Sending verification email to {} ", user.getEmail());
        String link = "http://localhost:8080/api/v1/auth/confirm?token=" + token;
        emailService.send(user.getEmail(),
                emailService.buildVerificationEmail(user.getFirstName(), link),
                EmailService.EMAIL_VERIFICATION_SUBJECT);

        return msg("confirmation email send successfully");
    }

    public String msg(String msg) {
        return "{\"success_msg\": \"" + msg + "\"}";
    }

}
