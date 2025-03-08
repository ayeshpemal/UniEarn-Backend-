package com.finalproject.uni_earn.dto.Paginated;



import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedJobDetailsResponseDTO {
    private List<JobDetailsResponseDTO> jobList;
    private long dataCount;
}
