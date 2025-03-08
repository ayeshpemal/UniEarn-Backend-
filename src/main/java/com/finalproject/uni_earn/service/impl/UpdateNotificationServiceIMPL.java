package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedNotificationResponseDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.UpdateNoRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateNotificationServiceIMPL implements UpdateNotificationService {

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private UpdateNoRepo notificationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void createNotification(Long applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        Job job = application.getJob();
        if (job == null) {
            throw new NotFoundException("Job not found for the application");
        }

        Employer employer = job.getEmployer();
        if (employer == null) {
            throw new NotFoundException("Employer not found for the job");
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

        NotificationDTO notificationDTO = new NotificationDTO(
                notification.getId(),
                message,
                job.getJobId(),
                notification.getIsRead(),
                notification.getSentDate()
        );

        // Send real-time notification to the specific recipient
        messagingTemplate.convertAndSendToUser(
                recipient.getUserName(),
                "/topic/notifications",
                notificationDTO
        );

        System.out.println("Notification sent to " + recipient.getUserName() + ": " + message);
    }

    @Override
    public boolean markNotificationAsRead(Long notificationId) {
        UpdateNotification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (notification.getIsRead()) {
            throw new AlreadyExistException("Notification is already marked as read");
        } else {
            notification.setIsRead(true);
            notificationRepo.save(notification);
            return true;
        }
    }

    @Override
    public PaginatedNotificationResponseDTO getPaginatedUpdateNotification(Long userId, int page, int size) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Long totalNotifications = notificationRepo.countByRecipient(user);

        Page<UpdateNotification> notifications = notificationRepo.findAllByRecipient(user, PageRequest.of(page, size));
        List<NotificationDTO> notificationDTOS = notifications.getContent().stream()
                .map(notification -> {
                    return new NotificationDTO(
                            notification.getId(),
                            notification.getMessage(),
                            notification.getApplication().getJob().getJobId(),
                            notification.getIsRead(),
                            notification.getSentDate()
                    );
                })
                .collect(Collectors.toList());
        return new PaginatedNotificationResponseDTO(notificationDTOS, totalNotifications);
    }

}
