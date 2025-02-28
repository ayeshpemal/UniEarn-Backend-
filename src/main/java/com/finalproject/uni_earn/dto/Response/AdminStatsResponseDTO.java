package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminStatsResponseDTO {
    private int totalJobsPosted;
    private Map<String, Integer> jobsByCategory;
    private List<String> mostAppliedJob;
    private List<String> leastAppliedJob;
    private String topEmployer;
    private String mostActiveStudent;
    private Map<String, Integer> jobsByLocation;
}
