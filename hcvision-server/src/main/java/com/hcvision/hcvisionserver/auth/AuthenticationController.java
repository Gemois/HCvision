package com.hcvision.hcvisionserver.auth;

import com.hcvision.hcvisionserver.auth.dto.AuthenticationRequest;
import com.hcvision.hcvisionserver.auth.dto.AuthenticationResponse;
import com.hcvision.hcvisionserver.auth.dto.RegisterRequest;
import com.hcvision.hcvisionserver.auth.dto.RegisterResponse;
import com.hcvision.hcvisionserver.auth.token.ConfirmationTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<ConfirmationTokenResponse> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(service.confirmToken(token));
    }

    @GetMapping(path = "confirmation-link")
    public ResponseEntity<String> sendConfirmationLink(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        try {
            service.sendConfirmationEmail(jwt);
            return ResponseEntity.ok("Confirmation email sent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}