package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.exception.EmailNotSendException;
import jakarta.validation.constraints.Email;
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

    public void sendEmail(@Email String email, String subject, String emailBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailNotSendException("Failed to send email to " + email);
        }
    }
}
