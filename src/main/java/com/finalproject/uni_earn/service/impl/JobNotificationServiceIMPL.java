package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.JobNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JobNotificationServiceIMPL implements JobNotificationService {

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

    @Autowired
    private FollowRepo followRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public String createFollowNotification(Employer employer, Job job) {
        List<Follow> followers = followRepo.findAllByEmployer(employer);

        String message = String.format(
                "A new %s job has been posted by %s: %s. Check it out!",
                job.getJobCategory(), // Assuming Job has a category field
                employer.getCompanyName(),
                job.getJobTitle()
        );

        for (Follow follow : followers) {
            Student student = follow.getStudent();

            JobNotification notification = new JobNotification();
            notification.setMessage(message);
            notification.setRecipient(student);
            notification.setJob(job);
            notification.setSentDate(new Date());
            notification.setIsRead(false);

            notificationRepo.save(notification);

            // Send real-time notification to the specific student
//            messagingTemplate.convertAndSendToUser(
//                    student.getUserName(),
//                    "/topic/notifications",
//                    notification
//            );

            System.out.println("Notification sent to student: " + student.getUserName());
        }

        return message; // Return only the message
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

