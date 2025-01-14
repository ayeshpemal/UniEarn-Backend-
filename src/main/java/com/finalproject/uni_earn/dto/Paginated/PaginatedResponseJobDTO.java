package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.JobDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedResponseJobDTO {
    private List<JobDTO> jobList;
    private long dataCount;
}
