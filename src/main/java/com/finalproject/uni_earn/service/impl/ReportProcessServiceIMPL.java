package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.enums.ReportType;
import com.finalproject.uni_earn.service.ReportProcessService;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class ReportProcessServiceIMPL implements ReportProcessService {
    private final Map<ReportType, Double> penaltyScores;

    public ReportProcessServiceIMPL() {
        penaltyScores = new EnumMap<>(ReportType.class);
        penaltyScores.put(ReportType.Harassment_and_Safety_Issues, 8.0);
        penaltyScores.put(ReportType.Fraud_and_Payment_Issues, 7.0);
        penaltyScores.put(ReportType.Inappropriate_Content, 6.0);
        penaltyScores.put(ReportType.Identity_Misrepresentation,5.0);
        penaltyScores.put(ReportType.Job_Misrepresentation,4.0);
        penaltyScores.put(ReportType.Professional_Conduct_Issues,3.0);
        penaltyScores.put(ReportType.Work_Environment_Concerns, 2.0);
        penaltyScores.put(ReportType.Other, 1.0);
    }

    public Double getScore(ReportType reportType) {
        return penaltyScores.getOrDefault(reportType, 0.0);
    }
}
