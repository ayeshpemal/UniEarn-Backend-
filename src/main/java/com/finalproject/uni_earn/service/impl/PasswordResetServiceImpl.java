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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${frontend.ip}")
    private String frontendIp;
    @Value("${backend.ip}")
    private String backendIp;

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
        String resetUrl = "http:/" + backendIp + ":8100/api/auth/reset-password?token=" + token;
        String emailBody =
                "Dear " + user.getUserName() + ",\n\n" +
                        "We received a request to reset your password for your UniEarn account.\n" +
                        "--------------------------------------------\n\n" +
                        "Please click the link below to reset your password:\n" +
                        resetUrl + "\n\n" +
                        "--------------------------------------------\n" +
                        //"This link will expire in 15 minutes for security reasons.\n\n" +
                        "If you didn't request a password reset, please ignore this email or contact support.\n\n" +
                        "Best regards,\n" +
                        "The UniEarn Team\n\n" +
                        "Note: This is an automated message, please do not reply directly to this email.";
        emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidValueException("Invalid or expired password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidValueException("Token has expired");
        }
        return "http://" + frontendIp + ":3000/reset-password?token=" + token;
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
