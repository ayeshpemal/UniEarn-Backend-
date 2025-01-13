package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;

import java.util.Date;

public class NotificationDTO {
    private Long id;

    private String message;

    private User recipient;

    private Job job;

    private Boolean isRead = false;

    private Date sentDate;
}
