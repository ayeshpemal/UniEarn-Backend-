package com.finalproject.uni_earn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${app.verification.url}")
    private String verificationUrl;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String link = verificationUrl + "?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Verify Your Email");
            message.setText("Please click the following link to verify your email: " + link);
            mailSender.send(message);
            System.out.println("Verification email sent successfully to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email to " + toEmail, e);
        }
    }
}
