package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.JobCategoryStatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedCategoryStaticsDTO {
    List<JobCategoryStatisticsDTO> categoryStaticsDTOList;
    Long totalCategoryStatics;
}
