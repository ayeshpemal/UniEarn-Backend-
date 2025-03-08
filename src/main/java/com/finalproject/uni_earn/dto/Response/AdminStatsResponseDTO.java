package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.UserDTO;
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
    private List<JobDTO> mostAppliedJob;
    private List<JobDTO> leastAppliedJob;
    private UserDTO topEmployer;
    private UserDTO mostActiveStudent;
    private Map<String, Integer> jobsByLocation;
}
