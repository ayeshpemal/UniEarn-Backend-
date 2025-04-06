package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobDetailsResponseDTO {
    private long jobId;
    private String jobTitle;
    private String jobCategory;
    private String jobDescription;
    private String jobLocation;
    private Date startDate;
    private Date endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private JobStatus jobStatus;
    private Double jobPayment;
    private int requiredWorkers;
    private List<Gender> requiredGender;
    private String applicationStatus; // For students: "PENDING", "ACCEPTED", etc.
    private Long applicationId; // For students: ID of the application
    private boolean isRated; // For students: true if the job is rated, false otherwise
    private Long employer;
}