package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;

public interface ReportService {
    public Reports submitReport(int reporterId, int reportedUserId, String feedback);
    public ReportDTO resolveReport(Long reportId, ReportState status);

    PaginatedReportDTO getallReports(int page , int size);

    PaginatedReportDTO getallReportsByCount(int id,int page, int size);
}
