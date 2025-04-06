package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.entity.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportDTO {
//    this is normal reportDTO uses in report service ,specially when you need all the details of the report, paged report entity give to data this type
    private Long reportId;
    private Long reporter;
    private Long reportedUser;
    private ReportType reportType;
    private String feedback;
    private LocalDateTime reportDate;
    private ReportState status;
}
