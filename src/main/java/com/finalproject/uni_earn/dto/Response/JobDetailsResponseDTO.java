package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobDetailsResponseDTO {
    private Integer jobId;
    private String jobTitle;
    private String jobCategory;
    private String jobLocation;
    private Date startDate;
    private Date endDate;
    private Boolean jobStatus;
    private Double jobPayment;
    private String applicationStatus; // For students: "PENDING", "ACCEPTED", etc.
}
