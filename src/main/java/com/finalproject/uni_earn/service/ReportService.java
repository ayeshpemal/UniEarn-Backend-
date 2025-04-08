package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.dto.request.ReportRequestDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;

public interface ReportService {
    String submitReport(ReportRequestDTO reportRequestDTO);

    ReportDTO resolveReport(Long reportId, ReportState status);

    PaginatedReportDTO getAllReports(int page , int size);

    PaginatedReportDTO getAllReportsById(Long userId,int page, int size);
}
