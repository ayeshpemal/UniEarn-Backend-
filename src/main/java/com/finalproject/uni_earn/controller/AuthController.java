package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.service.PasswordResetService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<StandardResponse> forgotPassword(@RequestParam String email) {
        passwordResetService.createPasswordResetTokenForUser(email);
        return new ResponseEntity<>(new StandardResponse(200, "Success", "Password reset link sent to email"), HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<StandardResponse> validateToken(@RequestParam String token) {
        String email = passwordResetService.validatePasswordResetToken(token);
        return new ResponseEntity<>(new StandardResponse(200, "Valid Token", email), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<StandardResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return new ResponseEntity<>(new StandardResponse(200, "Success", "Password has been reset"), HttpStatus.OK);
    }
}
