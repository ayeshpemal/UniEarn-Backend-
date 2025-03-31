package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.entity.enums.ReportType;

public interface ReportProcessService {

    public Double getScore(ReportType reportType);

    boolean isValidReportType(ReportType reportType);
}
