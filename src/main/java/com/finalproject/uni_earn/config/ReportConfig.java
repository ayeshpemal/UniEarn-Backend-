package com.finalproject.uni_earn.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportConfig {
    @Value("${report.threshold.warning:3}")
    private int warningThreshold;

    @Value("${report.threshold.critical:5}")
    private int criticalThreshold;

    @Value("${report.threshold.timeWindow:24}")
    private int timeWindowHours;
}
