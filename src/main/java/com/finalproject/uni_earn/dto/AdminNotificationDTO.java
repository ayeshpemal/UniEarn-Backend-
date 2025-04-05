package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminNotificationDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private Long recipientId;
    private Boolean isRead;
    private Date sentDate;
}
