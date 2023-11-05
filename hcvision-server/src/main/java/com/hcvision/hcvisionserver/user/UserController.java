package com.hcvision.hcvisionserver.user;

import com.hcvision.hcvisionserver.user.dto.ForgotPasswordRequest;
import com.hcvision.hcvisionserver.user.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping()
    public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(service.findUserByEmail(email));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> setPassword(@RequestBody ResetPasswordRequest setPasswordRequest) {
        try {
            service.resetPassword(setPasswordRequest);
            return ResponseEntity.ok("Password was reset successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/password/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            service.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok("Reset password email has been send");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
