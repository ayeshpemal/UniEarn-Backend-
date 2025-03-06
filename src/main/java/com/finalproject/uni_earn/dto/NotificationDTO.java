package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private Long job;
    private Boolean isRead = false;
    private Date sentDate;
}
