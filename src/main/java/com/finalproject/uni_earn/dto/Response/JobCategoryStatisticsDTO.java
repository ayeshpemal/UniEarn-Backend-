package com.finalproject.uni_earn.dto.Response;


import com.finalproject.uni_earn.entity.enums.JobCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobCategoryStatisticsDTO {
    private JobCategory jobCategory;
    private Long totalApplications;
}
