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

        switch (application.getStatus()) {
            case PENDING:
                // Notify employer about a new application
                message = String.format(
                        "New application received from %s for job: %s.",
                        application.getStudent() != null ? application.getStudent().getUserName() : "A Team",
                        job.getJobTitle()
                );
                recipient = employer;
                break;
            case ACCEPTED:
                // Notify student or team about application acceptance
                if (application.getStudent() != null) {
                    recipient = application.getStudent();
                    message = String.format(
                            "Congratulations! Your application for %s has been accepted.",
                            job.getJobTitle()
                    );
                } else if (application.getTeam() != null) {
                    recipient = application.getTeam().getLeader(); // Notify team leader
                    message = String.format(
                            "Congratulations! Your team's application for %s has been accepted.",
                            job.getJobTitle()
                    );
                } else {
                    throw new IllegalArgumentException("Application does not belong to a student or team");
                }
                break;

            case REJECTED:
                // Notify student or team about application rejection
                if (application.getStudent() != null) {
                    recipient = application.getStudent();
                    message = String.format(
                            "Your application for %s has been rejected.",
                            job.getJobTitle()
                    );
                } else if (application.getTeam() != null) {
                    recipient = application.getTeam().getLeader(); // Notify team leader
                    message = String.format(
                            "Your team's application for %s has been rejected.",
                            job.getJobTitle()
                    );
                } else {
                    throw new IllegalArgumentException("Application does not belong to a student or team");
                }
                break;

            case CONFIRMED:
                // Notify employer when a student confirms the job
                recipient = employer;
                message = String.format(
                        "Student %s has confirmed the job for %s.",
                        application.getStudent() != null ? application.getStudent().getUserName() : "A Team",
                        job.getJobTitle()
                );
                break;

            default:
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
