package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmployerBriefSummaryDTO {
    private Long totalJobCount;
    private Long pendingJobCount;
    private Long ongoingJobCount;
    private Long finishedJobCount;
    private Long canceledJobCount;

    // Categorized counts
    private Map<JobCategory, Long> jobCountByCategory;
    private Map<Location, Long> jobCountByLocation;

    // Most/least popular jobs
    private List<JobApplicationCountDTO> mostAppliedJobs;
    private List<JobApplicationCountDTO> leastAppliedJobs;
}
