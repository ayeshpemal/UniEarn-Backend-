package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.EmailRequest;
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

    private static final String RECEIVER_EMAIL = "pemalsandanuwan@gmail.com";

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

    public void sendUserEmail(EmailRequest request) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(request.getEmail()); // User's email
            mailMessage.setTo(RECEIVER_EMAIL); // Your email
            mailMessage.setSubject(request.getSubject());

            String content = "From: " + request.getFirstName() + " " + request.getLastName() +
                    "\nEmail: " + request.getEmail() +
                    "\nPhone: " + request.getPhone() +
                    "\n\nMessage:\n" + request.getMessage();

            mailMessage.setText(content);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailNotSendException("Failed to send email");
        }
    }
}
