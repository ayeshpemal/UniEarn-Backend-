package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.entity.User;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface AdminService {
    String makeUserAdmin(Long userId);
    String removeAdmin(Long userId);
    List<AdminResponseDTO> getAllAdmins();
    AdminStatsResponseDTO getPlatformStatistics();
    String broadcastNotification(String message);
    String sendNotificationToUser(Long userId, String message);
    String sendNotificationAllEmployers(String message);
    String sendNotificationAllStudents(String message);
}
