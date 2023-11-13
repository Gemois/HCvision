package com.hcvision.hcvisionserver.user;

import com.hcvision.hcvisionserver.user.dto.EditUserRequest;
import com.hcvision.hcvisionserver.user.dto.ForgotPasswordRequest;
import com.hcvision.hcvisionserver.user.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        service.deleteUser(jwt);
        return ResponseEntity.ok("User deleted along with all his information.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody EditUserRequest editUserRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        service.updateUser(editUserRequest, jwt);
        return ResponseEntity.ok("User profile updates successfully.");
    }

}
