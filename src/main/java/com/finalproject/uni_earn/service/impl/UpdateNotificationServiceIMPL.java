package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.UpdateNoRepo;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UpdateNotificationServiceIMPL implements UpdateNotificationService {

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private UpdateNoRepo notificationRepo;

    @Override
    public void createNotification(Long applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        Job job = application.getJob();
        if (job == null) {
            throw new IllegalArgumentException("Job not found for the application");
        }

        Employer employer = job.getEmployer();
        if (employer == null) {
            throw new IllegalArgumentException("Employer not found for the job");
        }

        String message;
        User recipient;

        if (application.getStatus() == ApplicationStatus.PENDING) {
            // Notify employer about a new application
            message = String.format(
                    "New application received from %s. Status: %s. Applied for: %s.",
                    application.getStudent() != null ? application.getStudent().getUserName() : "A Team",
                    application.getStatus(),
                    job.getJobTitle()
            );
            recipient = employer;
        } else if (application.getStatus() == ApplicationStatus.ACCEPTED || application.getStatus() == ApplicationStatus.REJECTED) {
            // Notify student or team about their application status
            if (application.getStudent() != null) {
                recipient = application.getStudent();
                message = String.format(
                        "Your application for %s has been %s.",
                        job.getJobTitle(),
                        application.getStatus().name().toLowerCase()
                );
            } else if (application.getTeam() != null) {
                recipient = application.getTeam().getLeader(); // Assuming a team has a leader to notify
                message = String.format(
                        "Your team's application for %s has been %s.",
                        job.getJobTitle(),
                        application.getStatus().name().toLowerCase()
                );
            } else {
                throw new IllegalArgumentException("Application does not belong to a student or team");
            }
        } else if (application.getStatus() == ApplicationStatus.CONFIRMED) {
            // Notify employer when a student confirms the job
            recipient = employer;
            message = String.format(
                    "Student %s has confirmed the job for %s.",
                    application.getStudent() != null ? application.getStudent().getUserName() : "A Team",
                    job.getJobTitle()
            );
        } else {
            throw new IllegalArgumentException("Invalid application status");
        }

        // Save notification
        UpdateNotification notification = new UpdateNotification();
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notification.setApplication(application);
        notification.setSentDate(new Date());
        notification.setIsRead(false);

        notificationRepo.save(notification);

        System.out.println("Notification sent to " + recipient.getUserName() + ": " + message);
    }

}
