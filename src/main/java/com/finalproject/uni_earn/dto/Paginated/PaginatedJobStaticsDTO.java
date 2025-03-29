package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.JobStaticsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedJobStaticsDTO {
    private List<JobStaticsDTO> jobStatics;
    private long totalJobs;
}
