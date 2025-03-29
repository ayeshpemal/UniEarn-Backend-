package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.JobSummeryDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class PaginatedJobSummeryDTO {
    List<JobSummeryDetails> jobSummeryDetails;
    long TotalItems;
}
