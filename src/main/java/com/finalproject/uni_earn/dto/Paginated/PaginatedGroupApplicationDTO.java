package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.NewGroupApplicationDTO;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedGroupApplicationDTO {
    private List<NewGroupApplicationDTO> groupApplications;
    private int applicationCount;
    private JobStatus jobStatus;
}
