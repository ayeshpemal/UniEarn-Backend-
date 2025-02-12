package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.config.ReportConfig;
import com.finalproject.uni_earn.repo.ReportRepo;

import java.time.LocalDateTime;

public class ReportAnalyticsServiceIMPL {

    final private ReportRepo reportRepo;
    final private ReportConfig reportConfig;

    public ReportAnalyticsServiceIMPL(ReportRepo reportRepo, ReportConfig reportConfig) {
        this.reportRepo = reportRepo;
        this.reportConfig = reportConfig;
    }


    public void analyzeUserReports(Long reportedUserId) {
        LocalDateTime timeWindow = LocalDateTime.now()
                .minusHours(reportConfig.getTimeWindowHours());

        long recentReportsCount = reportRepo
                .countByReportedUserIdAndReportDateAfter(
                        reportedUserId,
                        timeWindow
                );
        double totalPenaltyScore = reportRepo
                .sumPenaltyScoreByReportedUserIdAndReportDateAfter(
                        reportedUserId,
                        timeWindow
                );

//        // Analyze and notify
//        analyzeAndNotify(reportedUserId, recentReportsCount, totalPenaltyScore);
    }

}


