package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.JobCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobStaticsDTO {
    private Long JobId;
    private Long ApplicationCount;
    private JobCategory jobCategory;
    private Date StartDate;
    private Date EndDate;
}
