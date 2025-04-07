package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.config.ReportConfig;
import com.finalproject.uni_earn.dto.AdminNotificationDTO;
import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedNotificationResponseDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.AdminNotificationRepo;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.UpdateNoRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private ReportConfig reportConfig;

    @Autowired
    private AdminNotificationRepo adminNotificationRepo;

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
        List<User> recipient = new ArrayList<>();

        switch (application.getStatus()) {
            case INACTIVE:
                // Notify team members about the application being withdrawn
                if (application.getTeam() != null) {
                    message = String.format(
                            "%s has apply to the job: %s. as a team including you.",
                            application.getTeam().getLeader().getUserName(),
                            job.getJobTitle()
                    );
                    for (Student member : application.getTeam().getMembers()) {
                        if(!member.equals(application.getTeam().getLeader())) {
                            recipient.add(member);
                        }
                    }
                    break;
                } else {
                    throw new IllegalArgumentException("Application does not belong to a team");
                }
            case PENDING:
                // Notify employer about a new application
                message = String.format(
                        "New application received from %s for job: %s.",
                        application.getStudent() != null ? application.getStudent().getUserName() : "A Team",
                        job.getJobTitle()
                );
                recipient = Collections.singletonList(employer);
                break;
            case ACCEPTED:
                // Notify student or team about application acceptance
                if (application.getStudent() != null) {
                    recipient = Collections.singletonList(application.getStudent());
                    message = String.format(
                            "Congratulations! Your application for %s has been accepted.",
                            job.getJobTitle()
                    );
                } else if (application.getTeam() != null) {
                    recipient = Collections.singletonList(application.getTeam().getLeader()); // Notify team leader
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
                    recipient = Collections.singletonList(application.getStudent());
                    message = String.format(
                            "Your application for %s has been rejected.",
                            job.getJobTitle()
                    );
                } else if (application.getTeam() != null) {
                    recipient = Collections.singletonList(application.getTeam().getLeader()); // Notify team leader
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
                recipient = Collections.singletonList(employer);
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
        for (User user : recipient) {
            UpdateNotification notification = new UpdateNotification();
            notification.setMessage(message);
            notification.setRecipient(user);
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
            System.out.println("Member: " + user.getUserName());
            // Send real-time notification to the specific recipient
            messagingTemplate.convertAndSendToUser(
                    user.getUserName(),
                    "/topic/update-notifications",
                    notificationDTO
            );

            System.out.println("Notification sent to " + user.getUserName() + ": " + message);
        }
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

        Page<UpdateNotification> notifications = notificationRepo.findAllByRecipient(
                user, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
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

    @Override
    public void createReportAnalysisNotification(Long reportedUserId, long reportCount, double totalPenaltyScore) {
        User reportedUser = userRepo.findById(reportedUserId)
                .orElseThrow(() -> new NotFoundException("Reported user not found"));

        String message = getString(reportCount, totalPenaltyScore, reportedUser);

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(message);
        notification.setType(NotificationType.REPORT);
        notification.setIsRead(true);
        notification.setRecipient(userRepo.findById(reportedUserId)
            .orElseThrow(() -> new NotFoundException("User not found")));
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        //Send real-time notification to the admins

        AdminNotification notificationDTO = new AdminNotification(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRecipient(),
                notification.getIsRead(),
                notification.getSentDate()
        );

        AdminNotificationDTO adminNotificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRecipient().getUserId(),
                false,
                notification.getSentDate()
        );
        messagingTemplate.convertAndSendToUser(
                "admin",
                "/topic/report-notifications",
                adminNotificationDTO
        );
    }

    private String getString(long reportCount, double totalPenaltyScore, User reportedUser) {
        String message = null;

        if(totalPenaltyScore >= reportConfig.getWarningThreshold() && totalPenaltyScore < reportConfig.getCriticalThreshold()) {
            message = String.format(
                    "User %s has received a warning due to %d reports and a total penalty score of %.2f.",
                    reportedUser.getUserName(),
                    reportCount,
                    totalPenaltyScore
            );
        } else if (totalPenaltyScore >= reportConfig.getCriticalThreshold()) {
            message = String.format(
                    "User %s has received a warning due to %d reports and a total penalty score of %.2f. Immediate action is required.",
                    reportedUser.getUserName(),
                    reportCount,
                    totalPenaltyScore
            );
        }
        return message;
    }
}
