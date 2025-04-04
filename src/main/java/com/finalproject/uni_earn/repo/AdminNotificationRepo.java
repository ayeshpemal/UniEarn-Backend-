package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.AdminNotification;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface AdminNotificationRepo extends JpaRepository<AdminNotification, Long> {

    Page<AdminNotification> getByType(NotificationType type, Pageable pageable);

    Page<AdminNotification> getByTypeAndRecipient_UserId(NotificationType type, long recipientUserId, Pageable pageable);

    int countByTypeAndRecipient_UserId(NotificationType type, Long userId);

    int countByType(NotificationType type);
}
