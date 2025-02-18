package com.finalproject.uni_earn.dto.request;


import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.entity.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ReportRequestDTO {

    private long reporter;
    private long reportedUser;
    private ReportType reportType;
    private String feedback;
    private ReportState status;
    private int penaltyScore;

}
