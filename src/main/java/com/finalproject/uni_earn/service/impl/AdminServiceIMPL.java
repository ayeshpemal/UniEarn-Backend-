package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.AdminNotificationDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedAdminNotificationDTO;
import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.entity.AdminNotification;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.*;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.exception.NotificationFailedException;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.AdminService;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import com.finalproject.uni_earn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final EmployerRepo employerRepo;
    private final AdminNotificationRepo adminNotificationRepo;
    private final UserRepo userRepo;


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

        user.setRole(Role.EMPLOYER); // Defaulting back to a student, change if needed
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
                            "",
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
                            "",
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

        // Check if the message is empty
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidValueException("Notification message cannot be empty.");
        }

        // Remove leading/trailing quotes if any
        String cleanedMessage = message.trim();
        if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
            cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
        }

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(cleanedMessage);
        notification.setType(NotificationType.BROADCAST);
        notification.setIsRead(true);
        notification.setRecipient(null);
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        // Send notification to all users
        AdminNotificationDTO notificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                null,
                false,
                notification.getSentDate()
        );
        try {
            // Broadcast the message to all connected users
            messagingTemplate.convertAndSend(
                    "/topic/admin-notifications",
                    notificationDTO
            );
            return "Notification broadcasted successfully!";
        } catch (Exception e) {
            throw new NotificationFailedException("Failed to broadcast notification." + e.getMessage());
        }
    }

    @Override
    public String sendNotificationToUser(Long userId, String message) {
        // Check if the message is empty
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidValueException("Notification message cannot be empty.");
        }

        // Remove leading/trailing quotes if any
        String cleanedMessage = message.trim();
        if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
            cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
        }

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(cleanedMessage);
        notification.setType(NotificationType.USER_SPECIFIC);
        notification.setIsRead(false);
        notification.setRecipient(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found")));
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        // Send notification to the specific user
        AdminNotificationDTO notificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRecipient().getUserId(),
                notification.getIsRead(),
                notification.getSentDate()
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
            throw new NotificationFailedException("Failed to send notification to user: " + username);
        }
    }

    @Override
    public String sendNotificationAllEmployers(String message) {
        // Check if the message is empty
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidValueException("Notification message cannot be empty.");
        }

        // Remove leading/trailing quotes if any
        String cleanedMessage = message.trim();
        if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
            cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
        }

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(cleanedMessage);
        notification.setType(NotificationType.ALL_EMPLOYERS);
        notification.setIsRead(true);
        notification.setRecipient(null);
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        // Send notification to all employers
        AdminNotificationDTO notificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                null,
                false,
                notification.getSentDate()
        );

        try {
            messagingTemplate.convertAndSendToUser(
                    "employer", // Get the username of the user
                    "/topic/e-admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to all employers successfully!";
        } catch (Exception e) {
            throw new NotificationFailedException("Failed to send notification to all employers");
        }
    }

    @Override
    public String sendNotificationAllStudents(String message) {
        // Check if the message is empty
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidValueException("Notification message cannot be empty.");
        }

        // Remove leading/trailing quotes if any
        String cleanedMessage = message.trim();
        if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
            cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
        }

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(cleanedMessage);
        notification.setType(NotificationType.ALL_STUDENTS);
        notification.setIsRead(true);
        notification.setRecipient(null);
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        // Send notification to all students
        AdminNotificationDTO notificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                null,
                false,
                notification.getSentDate()
        );

        try {
            messagingTemplate.convertAndSendToUser(
                    "student", // Get the username of the user
                    "/topic/s-admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to all students successfully!";
        } catch (Exception e) {
            throw new NotificationFailedException("Failed to send notification to all students");
        }
    }

    @Override
    public String sendNotificationAllAdmins(String message) {
        // Check if the message is empty
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidValueException("Notification message cannot be empty.");
        }

        // Remove leading/trailing quotes if any
        String cleanedMessage = message.trim();
        if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
            cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
        }

        // Store the notification in the database
        AdminNotification notification = new AdminNotification();
        notification.setMessage(cleanedMessage);
        notification.setType(NotificationType.ALL_ADMINS);
        notification.setIsRead(true);
        notification.setRecipient(null);
        notification.setSentDate(new Date());
        adminNotificationRepo.save(notification);

        // Send notification to all admins
        AdminNotificationDTO notificationDTO = new AdminNotificationDTO(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.getType(),
                null,
                false,
                notification.getSentDate()
        );

        try {
            messagingTemplate.convertAndSendToUser(
                    "admin", // Get the username of the user
                    "/topic/a-admin-notifications", // Changed to match the new subscription
                    notificationDTO
            );
            return "Notification sent to all admins successfully!";
        } catch (Exception e) {
            throw new NotificationFailedException("Failed to send notification to all admins");
        }
    }

    @Override
    public PaginatedAdminNotificationDTO getPrivateAdminNotifications(Long userId, NotificationType type, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidValueException("Page number and size must be greater than zero");
        }

        PaginatedAdminNotificationDTO paginatedAdminNotificationDTO = new PaginatedAdminNotificationDTO();

        if(type == NotificationType.REPORT || type == NotificationType.USER_SPECIFIC){
            List<AdminNotificationDTO> adminNotificationDTOs;
            if(userId == null){
                Page<AdminNotification> notifications = adminNotificationRepo.getByType(type, PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "updatedAt")));
                List<AdminNotification> adminNotifications = notifications.getContent();
                adminNotificationDTOs = adminNotifications.stream()
                        .map(notification -> modelMapper.map(notification, AdminNotificationDTO.class))
                        .toList();
            }else{
                if(!userRepo.existsByUserId(userId)){
                    throw new NotFoundException("User with ID " + userId + " not found");
                }
                Page<AdminNotification> notifications = adminNotificationRepo.getByTypeAndRecipient_UserId(type, userId, PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "updatedAt")));
                List<AdminNotification> adminNotifications = notifications.getContent();
                adminNotificationDTOs = adminNotifications.stream()
                        .map(notification -> modelMapper.map(notification, AdminNotificationDTO.class))
                        .toList();
            }

            paginatedAdminNotificationDTO.setNotifications(adminNotificationDTOs);
            paginatedAdminNotificationDTO.setTotalNotifications(adminNotificationRepo.countByTypeAndRecipient_UserId(type, userId));
        } else {
            Page<AdminNotification> notifications = adminNotificationRepo.getByType(type, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
            List<AdminNotification> adminNotifications = notifications.getContent();
            List<AdminNotificationDTO> adminNotificationDTOs = adminNotifications.stream()
                    .map(notification -> modelMapper.map(notification, AdminNotificationDTO.class))
                    .toList();
            paginatedAdminNotificationDTO.setNotifications(adminNotificationDTOs);
            paginatedAdminNotificationDTO.setTotalNotifications(adminNotificationRepo.countByType(type));
        }

        return paginatedAdminNotificationDTO;
    }

    @Override
    public void generateDummyUsers(UserService userService) {
        // Create 4 employers
        createDummyEmployers(userService);

        // Create 7 students
        createDummyStudents(userService);

        System.out.println("Successfully created 11 dummy users!");
    }

    private void createDummyEmployers(UserService userService) {
        // Company data for employers
        List<String> companyNames = List.of(
                "QuickServe Retail",
                "SalesPro Enterprises",
                "RetailMart Solutions",
                "EduTutor Academy",
                "GourmetCater Services",
                "GourmetCater Services",
                "FastFood Express"

        );

        List<String> companyDetails = List.of(
                "Leading retail chain with multiple checkout counters requiring efficient cashiers for customer transactions.",
                "Sales-focused company providing promotional staff and salespeople for various retail and event locations.",
                "Large retail organization with multiple store locations specializing in consumer goods and electronics.",
                "Educational tutoring company connecting qualified tutors with students for various academic subjects.",
                "Full-service catering company specializing in events, corporate functions, and private parties.",
                "Full-service catering company specializing in events, corporate functions, and private parties.",
                "Quick-service restaurant chain specializing in fast food with multiple locations across the country."
        );

        List<List<String>> categoriesList = List.of(
                List.of("CASHIER", "RETAIL"),
                List.of("SALESMEN", "EVENT_BASED"),
                List.of("RETAIL", "STORE_HELPER"),
                List.of("TUTORING", "DATA_ENTRY"),
                List.of("CATERING", "FOOD_AND_BEVERAGE"),
                List.of("CATERING", "FOOD_AND_BEVERAGE"),
                List.of("FOOD_AND_BEVERAGE", "KITCHEN_HELPER")
        );

        List<Location> locations = List.of(
                Location.COLOMBO,
                Location.KANDY,
                Location.GALLE,
                Location.JAFFNA,
                Location.MATARA,
                Location.GAMPAHA,
                Location.KALUTARA
        );

        for (int i = 0; i < companyNames.size(); i++) {
            UserRequestDTO employer = new UserRequestDTO();
            employer.setUserName("employer" + (i+1));
            employer.setEmail("employer" + (i+1) + "@example.com");
            employer.setPassword("Password@123");
            employer.setRole(Role.EMPLOYER);
            employer.setCompanyName(companyNames.get(i));
            employer.setCompanyDetails(companyDetails.get(i));
            employer.setCategories(categoriesList.get(i));
            employer.setLocation(locations.get(i).toString());
            employer.setContactNumbers(List.of("077" + (1000000 + i*111)));

            try {
                String result = userService.registerUser(employer);
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Failed to create employer " + (i+1) + ": " + e.getMessage());
            }
        }
    }

    private void createDummyStudents(UserService userService) {
        List<String> displayNames = List.of(
                "Amal Perera",
                "Nisha Fernando",
                "Ravi Gunawardena",
                "Sameera Silva",
                "Malini Jayasuriya",
                "Kasun Rajapakse",
                "Tharushi Mendis",
                "Dilani Kumari"
        );

        List<String> universities = List.of(
                "University of Colombo",
                "University of Moratuwa",
                "University of Peradeniya",
                "University of Sri Jayewardenepura",
                "University of Kelaniya",
                "University of Jaffna",
                "Uva Wellassa University",
                "Wayamba University"
        );

        List<Gender> genders = List.of(
                Gender.MALE,
                Gender.FEMALE,
                Gender.MALE,
                Gender.MALE,
                Gender.FEMALE,
                Gender.MALE,
                Gender.FEMALE,
                Gender.FEMALE
        );

        List<Location> locations = List.of(
                Location.COLOMBO,
                Location.GAMPAHA,
                Location.KANDY,
                Location.GALLE,
                Location.MATARA,
                Location.KALUTARA,
                Location.KURUNEGALA,
                Location.JAFFNA
        );

        List<List<String>> skillsList = List.of(
                List.of("Java", "Spring Boot", "React"),
                List.of("Graphic Design", "UI/UX", "Adobe Suite"),
                List.of("Data Analysis", "Python", "SQL"),
                List.of("Flutter", "Firebase", "Android Development"),
                List.of("Content Writing", "SEO", "Digital Marketing"),
                List.of("Network Administration", "Linux", "Cloud Computing"),
                List.of("Machine Learning", "TensorFlow", "Data Science"),
                List.of("Typing", "Data Entry", "Microsoft Office")
        );

        List<List<String>> preferencesList = List.of(
                List.of("WEB_DEVELOPER", "DATA_ENTRY"),
                List.of("DESIGN", "RETAIL"),
                List.of("DATA_ENTRY", "TUTORING"),
                List.of("WEB_DEVELOPER", "TYPING"),
                List.of("CONTENT_WRITING", "MARKETING"),
                List.of("TUTORING", "OTHER"),
                List.of("DATA_ENTRY", "TUTORING"),
                List.of("WEB_DEVELOPER", "DATA_ENTRY")

        );

        for (int i = 0; i < displayNames.size(); i++) {
            UserRequestDTO student = new UserRequestDTO();
            student.setUserName("student" + (i+1));
            student.setEmail("student" + (i+1) + "@example.com");
            student.setPassword("Password@123");
            student.setRole(Role.STUDENT);
            student.setDisplayName(displayNames.get(i));
            student.setUniversity(universities.get(i));
            student.setGender(genders.get(i).toString());
            student.setSkills(skillsList.get(i));
            student.setPreferences(preferencesList.get(i));
            student.setLocation(locations.get(i).toString());
            student.setContactNumbers(List.of("071" + (1000000 + i*111)));

            try {
                String result = userService.registerUser(student);
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Failed to create student " + (i+1) + ": " + e.getMessage());
            }
        }
    }
}
