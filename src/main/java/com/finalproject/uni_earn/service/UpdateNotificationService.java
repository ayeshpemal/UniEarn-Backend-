package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedNotificationResponseDTO;

public interface UpdateNotificationService {
    void createNotification(Long applicationId);
    boolean markNotificationAsRead(Long notificationId);
    PaginatedNotificationResponseDTO getPaginatedUpdateNotification(Long userId, int page, int size);
}
