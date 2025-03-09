package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationDTO {
    private Long applicationId;
    private Long jobId; // Assume this is mapped to Job entity
    private Long userId; // Assume this is mapped to User entity
    private Long teamId;
    private String status; // String representation of ApplicationStatus
    private Date appliedDate;



}
