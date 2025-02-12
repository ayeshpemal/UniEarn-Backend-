package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ReportState;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
public class ReportDTO {
//    this is normal reportDTO uses in report service ,specially when you need all the details of the report, paged report entity give to data this type
    private Long reportId;
    private User reporter;
    private User reportedUser;
    private String feedback;
    private LocalDateTime reportDate;
    private ReportState status;
}
