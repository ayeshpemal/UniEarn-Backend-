package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.dto.request.ReportRequestDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;

public interface ReportService {
    public Reports submitReport(ReportRequestDTO reportRequestDTO);

    public ReportDTO resolveReport(Long reportId, ReportState status);

    PaginatedReportDTO getAllReports(int page , int size);

    PaginatedReportDTO getAllReportsById(int id,int page, int size);
}
