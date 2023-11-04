package com.hcvision.hcvisionserver.auth;


import com.hcvision.hcvisionserver.config.JwtService;
import com.hcvision.hcvisionserver.user.Role;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserRepository;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }


    public AuthenticationResponse register(RegisterRequest request) {
        boolean userExists = repository.findByEmail(request.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException("email already taken");
        }

        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }
        var user = User.builder()
                        .firstName(request.getFirstname())
                        .lastName(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }

}
