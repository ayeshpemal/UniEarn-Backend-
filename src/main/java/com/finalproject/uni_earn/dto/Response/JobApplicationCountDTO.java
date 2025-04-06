package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobApplicationCountDTO {
    private Long jobId;
    private String jobTitle;
    private Long applicationCount;
}
