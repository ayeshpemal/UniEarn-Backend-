package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.PasswordResetToken;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.PasswordResetTokenRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.PasswordResetService;
import com.finalproject.uni_earn.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordResetTokenRepo tokenRepository;

    @Autowired
    private EmailService emailService; // You already have email sending configured!

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createPasswordResetTokenForUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        // Delete any existing token for the user
        //tokenRepository.deleteByUser_UserId(user.getUserId());

        // Create a new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Token valid for 15 minutes

        tokenRepository.save(resetToken);

        // Send reset email
        String resetUrl = "http://localhost:8100/api/auth/reset-password?token=" + token;
        String emailBody = "Click the link to reset your password: " + resetUrl;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidValueException("Invalid or expired password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidValueException("Token has expired");
        }
        return "http://localhost:3000/reset-password?token=" + token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidValueException("Invalid password reset token"));

        if(!PasswordValidator.isValidPassword(newPassword)) {
            throw new InvalidValueException("Password does not meet complexity requirements");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Invalidate the token after password reset
        tokenRepository.delete(resetToken);
    }
}
