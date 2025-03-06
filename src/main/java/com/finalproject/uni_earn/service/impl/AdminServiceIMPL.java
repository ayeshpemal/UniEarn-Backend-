package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.User;
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

    public AdminStatsResponseDTO getPlatformStatistics() {
        AdminStatsResponseDTO response = new AdminStatsResponseDTO();

        try {
            // Total jobs posted
            long jobCount = jobRepository.count();
            if (jobCount == 0) {
                throw new NotFoundException("No jobs found on the platform.");
            }
            response.setTotalJobsPosted((int) jobCount);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while counting jobs.", e);
        }

        try {
            // Jobs by category
            Map<String, Integer> jobsByCategory = jobRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(
                            job -> job.getJobCategory().toString(),
                            Collectors.summingInt(job -> 1)
                    ));
            response.setJobsByCategory(jobsByCategory.isEmpty() ? Collections.emptyMap() : jobsByCategory);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by category.", e);
        }

        try {
            // Most applied job
            for (Long id : jobRepository.findMostAppliedJob()) {
                JobDTO jobDTO = jobService.viewJobDetails(id);
                if (response.getMostAppliedJob() == null) {
                    response.setMostAppliedJob(new ArrayList<>());
                }
                response.getMostAppliedJob().add(jobDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the most applied job.", e);
        }

        try {
            // Least applied job
            for (Long id : jobRepository.findLeastAppliedJob()) {
                JobDTO jobDTO = jobService.viewJobDetails(id);
                if (response.getLeastAppliedJob() == null) {
                    response.setLeastAppliedJob(new ArrayList<>());
                }
                response.getLeastAppliedJob().add(jobDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the least applied job.", e);
        }

        try {
            // Top employer (by job count)
            Optional<UserDTO> topEmployer = userRepository.findTopEmployer()
                    .map(tuple -> new UserDTO(
                            tuple.get("userId", Long.class),
                            tuple.get("userName", String.class),
                            tuple.get("email", String.class),
                            tuple.get("role", String.class)
                    ));
            response.setTopEmployer(Objects.equals(topEmployer.get().getRole(), "EMPLOYER") ? topEmployer.get() : null);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the top employer.", e);
        }

        try {
            // Most active student (by applications)
            Optional<UserDTO> mostActiveStudent = userRepository.findMostActiveStudent()
                    .map(tuple -> new UserDTO(
                            tuple.get("userId", Long.class),
                            tuple.get("userName", String.class),
                            tuple.get("email", String.class),
                            tuple.get("role", String.class)
                    ));

            response.setMostActiveStudent(Objects.equals(mostActiveStudent.get().getRole(), "STUDENT") ? mostActiveStudent.get() : null);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the most active student.", e);
        }

        try {
            // Jobs by location
            Map<String, Integer> jobsByLocation = jobRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(
                            job -> job.getJobLocations().toString(),
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue) // Convert Long to Integer
                    ));
            response.setJobsByLocation(jobsByLocation.isEmpty() ? Collections.emptyMap() : jobsByLocation);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by location.", e);
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
