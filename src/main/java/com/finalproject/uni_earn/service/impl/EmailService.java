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
            //Send email to system
            SimpleMailMessage mailMessageToSystem = new SimpleMailMessage();
            mailMessageToSystem.setFrom(request.getEmail()); // User's email
            mailMessageToSystem.setTo(RECEIVER_EMAIL); // Your email
            mailMessageToSystem.setSubject(request.getSubject());

            String content =
                    "From: " + request.getFirstName() + " " + request.getLastName() + "\n" +
                            "Email: " + request.getEmail() + "\n" +
                            "Phone: " + request.getPhone() + "\n\n" +
                            "Message:\n" +
                            "--------------------\n" +
                            request.getMessage() + "\n" +
                            "--------------------";

            mailMessageToSystem.setText(content);
            mailSender.send(mailMessageToSystem);

            // Send email to user
            SimpleMailMessage mailMessageToUser = new SimpleMailMessage();
            mailMessageToUser.setFrom(RECEIVER_EMAIL); // Your email
            mailMessageToUser.setTo(request.getEmail()); // User's email
            mailMessageToUser.setSubject("Confirmation: " + request.getSubject());
            mailMessageToUser.setText(
                    "Dear " + request.getFirstName() + ",\n\n" +
                            "Thank you for contacting us. We have received your message and will get back to you shortly.\n\n" +
                            "Your message details:\n" +
                            "--------------------\n" +
                            "Subject: " + request.getSubject() + "\n" +
                            "Message: " + request.getMessage() + "\n\n" +
                            "If you have any other questions, please don't hesitate to contact us.\n\n" +
                            "Best regards,\n" +
                            "The UniEarn Team\n" +
                            "--------------------\n" +
                            "This is an automated message, please do not reply directly to this email."
            );
            mailSender.send(mailMessageToUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailNotSendException("Failed to send email");
        }
    }
}
