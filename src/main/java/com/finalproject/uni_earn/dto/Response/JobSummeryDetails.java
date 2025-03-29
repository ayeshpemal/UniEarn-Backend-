package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobSummeryDetails {

    private long jobId;
    private String jobTitle;
    private String jobDescription;
    private Date startDate;
    private Date endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private JobStatus jobStatus;

}
