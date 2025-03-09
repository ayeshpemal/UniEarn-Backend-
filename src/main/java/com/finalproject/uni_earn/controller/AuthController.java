package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.request.ForgetPasswordDTO;
import com.finalproject.uni_earn.dto.request.UpdatePasswordDTO;
import com.finalproject.uni_earn.service.PasswordResetService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView validateToken(@RequestParam String token) {
        String url = passwordResetService.validatePasswordResetToken(token);
        return new RedirectView(url);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<StandardResponse> resetPassword(@RequestBody ForgetPasswordDTO request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return new ResponseEntity<>(new StandardResponse(200, "Success", "Password has been reset"), HttpStatus.OK);
    }
}
