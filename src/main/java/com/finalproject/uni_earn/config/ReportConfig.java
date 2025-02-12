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
    @Value("${report.threshold.warning}")
    private int warningThreshold = 3;

    @Value("${report.threshold.critical}")
    private int criticalThreshold = 5;

    @Value("${report.threshold.timeWindow}")
    private int timeWindowHours = 24;
}
