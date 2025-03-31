package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.NewStudentApplicationDTO;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedStudentApplicationDTO {
    private List<NewStudentApplicationDTO> studentApplications;
    private int applicationCount;
    private JobStatus jobStatus;
}
