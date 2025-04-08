package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ReportState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedReportDTO {
//    this is for to return the report details to the admin as pages. uses in return type of get all in report controller
    private List<ReportDTO> ReportDTOList;
    private Long totalPenaltyScore;
    private Long totalItems;
}
