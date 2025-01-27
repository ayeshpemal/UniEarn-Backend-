package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationServiceIMPL implements NotificationService {

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private EmployerRepo employerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private  ApplicationRepo applicationRepo;


    public void createNotification(Long applicationId) {

        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));


        Job job = application.getJob();
        if (job == null) {
            throw new IllegalArgumentException("Job not found for the application");
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


        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(job.getEmployer());
        notification.setJob(job);
        notification.setSentDate(new Date());
        notification.setIsRead(false);

        notificationRepo.save(notification);


        System.out.println("Generated Notification Message: " + message);
    }



    public boolean markAsRead(Long id) {
        return notificationRepo.findById(id).map(notification -> {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notificationRepo.save(notification);
                return true;
            }
            return false;
        }).orElse(false);
    }
}

