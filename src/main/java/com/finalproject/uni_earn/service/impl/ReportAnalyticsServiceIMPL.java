package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.config.ReportConfig;
import com.finalproject.uni_earn.repo.ReportRepo;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportAnalyticsServiceIMPL {

    final private ReportRepo reportRepo;
    final private ReportConfig reportConfig;
    final private UpdateNotificationService updateNotificationService;

    public ReportAnalyticsServiceIMPL(ReportRepo reportRepo, ReportConfig reportConfig, UpdateNotificationService updateNotificationService) {
        this.reportRepo = reportRepo;
        this.reportConfig = reportConfig;
        this.updateNotificationService = updateNotificationService;
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

        // Analyze and notify
        if(totalPenaltyScore >= reportConfig.getWarningThreshold()) {
            // Notify the admin or take action
            updateNotificationService.createReportAnalysisNotification(reportedUserId, recentReportsCount, totalPenaltyScore);
        }
    }
}


