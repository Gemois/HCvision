package com.hcvision.hcvisionserver.auth;

import com.hcvision.hcvisionserver.auth.dto.AuthenticationRequest;
import com.hcvision.hcvisionserver.auth.dto.AuthenticationResponse;
import com.hcvision.hcvisionserver.auth.dto.RegisterRequest;
import com.hcvision.hcvisionserver.auth.dto.RegisterResponse;
import com.hcvision.hcvisionserver.auth.token.dto.ConfirmationTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(value = "/register", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping(value = "/authenticate", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping(value = "confirm", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ConfirmationTokenResponse> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(service.confirmToken(token));
    }

    @GetMapping(value = "confirmation-link", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> sendConfirmationLink(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        service.sendConfirmationEmail(jwt);
        return ResponseEntity.ok().build();
    }
}