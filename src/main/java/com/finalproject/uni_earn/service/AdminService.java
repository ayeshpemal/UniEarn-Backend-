package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedAdminNotificationDTO;
import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    String makeUserAdmin(Long userId);
    String removeAdmin(Long userId);
    List<AdminResponseDTO> getAllAdmins();
    AdminStatsResponseDTO getPlatformStatistics(LocalDateTime startDate, LocalDateTime endDate);
    String broadcastNotification(String message);
    String sendNotificationToUser(Long userId, String message);
    String sendNotificationAllEmployers(String message);
    String sendNotificationAllStudents(String message);
    String sendNotificationAllAdmins(String message);
    PaginatedAdminNotificationDTO getPrivateAdminNotifications(Long userId,NotificationType type, int page, int size);
    void generateDummyUsers(UserService userService);
}
