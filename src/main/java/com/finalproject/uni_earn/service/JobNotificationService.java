package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedNotificationResponseDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;

public interface JobNotificationService {

    String createFollowNotification(Employer employer, Job job);
    boolean markAsRead(Long id);
    PaginatedNotificationResponseDTO getPaginatedJobNotification(Long userId, int page, int size);
}
