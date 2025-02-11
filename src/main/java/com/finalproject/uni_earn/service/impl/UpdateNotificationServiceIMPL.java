package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.*;
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

        User student = application.getStudent();
        if (!(student instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }
        Student studentDetails = (Student) student;

        String university = studentDetails.getUniversity() != null ? studentDetails.getUniversity() : "Unknown University";
        String message = String.format(
                "New application received from %s (%s). Status: %s. Applied for: %s.",
                studentDetails.getUserName(),
                university,
                application.getStatus(),
                job.getJobTitle()
        );


        UpdateNotification notification = new UpdateNotification();
        notification.setMessage(message);
        notification.setRecipient(employer); // Employer is the recipient
        notification.setApplication(application);
        notification.setSentDate(new Date());
        notification.setIsRead(false);

        notificationRepo.save(notification);

        System.out.println("Notification sent to employer: " + message);
    }
}
