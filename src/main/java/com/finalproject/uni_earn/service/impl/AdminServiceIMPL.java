package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.exception.NotificationFailedException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.AdminService;
import com.finalproject.uni_earn.service.JobService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceIMPL implements AdminService {
    private final JobRepo jobRepository;
    private final ApplicationRepo applicationRepository;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;
    private final JobService jobService;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public String makeUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new AlreadyExistException("User is already an admin.");
        }

        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return "User with ID " + userId + " is now an admin.";
    }

    /**
     * Remove admin privileges from a user.
     */
    @Transactional
    public String removeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new NotFoundException("User is not an admin.");
        }

        user.setRole(Role.STUDENT); // Defaulting back to a student, change if needed
        user.setDeleted(true);
        userRepository.save(user);
        return "User with ID " + userId + " is no longer an admin.";
    }

    /**
     * Get a list of all admins in the system.
     */
    public List<AdminResponseDTO> getAllAdmins() {
        List<User> user = userRepository.findByRole(Role.ADMIN);
        return user.stream()
                .map(u -> modelMapper.map(u, AdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    public AdminStatsResponseDTO getPlatformStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        AdminStatsResponseDTO response = new AdminStatsResponseDTO();

        try {
            long jobCount = jobRepository.countJobsByDateRange(startDate, endDate);
            response.setTotalJobsPosted((int) jobCount);
        } catch (Exception e) {
            throw new RuntimeException("Error while counting jobs within date range.", e);
        }

        try {
            long userCount = userRepository.countByCreatedAtBeforeAndIsDeletedAndRoleNot(endDate, false, Role.ADMIN);
            response.setActiveUsers((int) userCount);
        } catch (Exception e) {
            throw new RuntimeException("Error while counting users within date range.", e);
        }

        try {
            long applicationCount = applicationRepository.countByCreatedAtBetween(startDate, endDate);
            response.setTotalApplications((int) applicationCount);
        } catch (Exception e) {
            throw new RuntimeException("Error while counting applications within date range.", e);
        }

        try {
            long completedJobsCount = jobRepository.countByCreatedAtBetweenAndJobStatus(startDate, endDate, JobStatus.FINISH);
            response.setCompletedJobs((int) completedJobsCount);
        } catch (Exception e) {
            throw new RuntimeException("Error while counting completed jobs within date range.", e);
        }

        try {
            Map<String, Integer> jobsByCategory = jobRepository.countJobsByCategory(startDate, endDate)
                    .stream()
                    .collect(Collectors.toMap(
                            obj -> obj[0].toString(),
                            obj -> ((Number) obj[1]).intValue()
                    ));
            response.setJobsByCategory(jobsByCategory);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by category within date range.", e);
        }

        try {
            // ✅ Get job count by location
            List<Job> jobsByLocationList = jobRepository.findJobsByDateRange(startDate, endDate);

            Map<String, Integer> jobsByLocation = jobsByLocationList.stream()
                    .collect(Collectors.groupingBy(
                            job -> job.getJobLocations().toString(),
                            Collectors.summingInt(job -> 1)
                    ));
            response.setJobsByLocation(jobsByLocation);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by location within date range.", e);
        }

        try {
            List<Long> mostAppliedJobs = jobRepository.findMostAppliedJobByDate(startDate, endDate);
            response.setMostAppliedJob(mostAppliedJobs.isEmpty() ? null : mostAppliedJobs.stream()
                    .map(jobService::viewJobDetails)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving most applied jobs within date range.", e);
        }

        try {
            List<Long> leastAppliedJobs = jobRepository.findLeastAppliedJobByDate(startDate, endDate);
            response.setLeastAppliedJob(leastAppliedJobs.isEmpty() ? null : leastAppliedJobs.stream()
                    .map(jobService::viewJobDetails)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving least applied jobs within date range.", e);
        }

        try {
            Optional<UserDTO> topEmployer = userRepository.findTopEmployerByDate(startDate, endDate)
                    .map(tuple -> new UserDTO(
                            tuple.get("userId", Long.class),
                            tuple.get("userName", String.class),
                            tuple.get("email", String.class),
                            tuple.get("role", String.class),
                            null
                    ));
            response.setTopEmployer(topEmployer.orElse(null));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving top employer within date range.", e);
        }

        try {
            Optional<UserDTO> mostActiveStudent = userRepository.findMostActiveStudentByDate(startDate, endDate)
                    .map(tuple -> new UserDTO(
                            tuple.get("userId", Long.class),
                            tuple.get("userName", String.class),
                            tuple.get("email", String.class),
                            tuple.get("role", String.class),
                            null
                    ));
            response.setMostActiveStudent(mostActiveStudent.orElse(null));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving most active student within date range.", e);
        }

        return response;
    }

    @Override
    public String broadcastNotification(String message) {
        // Send notification to all users

        NotificationDTO notificationDTO = new NotificationDTO(
                null,
                message,
                null,
                null,
                new Date()
        );
        try {
            // Broadcast the message to all connected users
            messagingTemplate.convertAndSend("/topic/admin-notifications", notificationDTO);
            return "Notification broadcasted successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotificationFailedException("Failed to broadcast notification.");
        }
    }

    @Override
    public String sendNotificationToUser(Long userId, String message) {
        NotificationDTO notificationDTO = new NotificationDTO(
                null,
                message,
                null,
                null,
                new Date()
        );
        String username = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"))
                .getUserName();
        try {
            messagingTemplate.convertAndSendToUser(
                    username, // Get the username of the user
                    "/topic/admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to " + username + " successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotificationFailedException("Failed to send notification to user: " + username);
        }
    }

    @Override
    public String sendNotificationAllEmployers(String message) {
        // Send notification to all employers
        NotificationDTO notificationDTO = new NotificationDTO(
                null,
                message,
                null,
                null,
                new Date()
        );

        try {
            messagingTemplate.convertAndSendToUser(
                    "employer", // Get the username of the user
                    "/topic/admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to all employers successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotificationFailedException("Failed to send notification to all employers");
        }
    }

    @Override
    public String sendNotificationAllStudents(String message) {
        NotificationDTO notificationDTO = new NotificationDTO(
                null,
                message,
                null,
                null,
                new Date()
        );

        try {
            messagingTemplate.convertAndSendToUser(
                    "student", // Get the username of the user
                    "/topic/admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to all students successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotificationFailedException("Failed to send notification to all students");
        }
    }
}
