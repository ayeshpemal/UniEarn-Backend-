package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.config.ReportConfig;
import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.dto.request.ReportRequestDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ReportRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ReportProcessService;
import com.finalproject.uni_earn.service.ReportService;
import com.finalproject.uni_earn.util.mappers.ReportMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceIMPL implements ReportService {
    // remember about int reporterId, int reportedUserId, because it is LONG on the Report entity , Think it is better user ids are Long not int
    final private ReportRepo reportRepository;
    final private UserRepo userRepository;
    final private ReportMapper reportMapper;
    final private ReportProcessService reportProcessService;
    final private ReportAnalyticsServiceIMPL reportAnalyticsService;
    final private ReportConfig reportConfig;

    @Autowired
    public ReportServiceIMPL(ReportRepo reportRepository, UserRepo userRepository, ReportMapper reportMapper, ReportProcessService reportProcessService, ReportAnalyticsServiceIMPL reportAnalyticsService, ReportConfig reportConfig) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
        this.reportProcessService = reportProcessService;
        this.reportAnalyticsService = reportAnalyticsService;
        this.reportConfig = reportConfig;
    }


    @Override
    public String submitReport(ReportRequestDTO reportRequestDTO) {
        // Check if the reporter and reported user exist
        if (!userRepository.existsById(reportRequestDTO.getReporter())) {
            throw new NotFoundException("Reporter not found");
        }
        if (!userRepository.existsById(reportRequestDTO.getReportedUser())) {
            throw new NotFoundException("Reported user not found");
        }
        // Check if the report type is valid
        if (!reportProcessService.isValidReportType(reportRequestDTO.getReportType())) {
            throw new NotFoundException("Invalid report type");
        }
        if(reportRequestDTO.getReporter() == reportRequestDTO.getReportedUser()) {
            throw new NotFoundException("You cannot report yourself");
        }

        Double penalty_Score = reportProcessService.getScore(reportRequestDTO.getReportType());
        Reports report = new Reports(
                null,
                userRepository.findById(reportRequestDTO.getReporter()).orElseThrow(),
                userRepository.findById(reportRequestDTO.getReportedUser()).orElseThrow(),
                reportRequestDTO.getReportType(),
                reportRequestDTO.getFeedback(),
                null,
                ReportState.PENDING,
                penalty_Score
        );
        reportRepository.save(report);

        reportAnalyticsService.analyzeUserReports(reportRequestDTO.getReportedUser());
        return "Report submitted successfully";
    }

    // Here we take input as int ,due to user table have int as id, but in report table it is long
    @Override
    public ReportDTO resolveReport(Long reportId, ReportState status) {
        Reports report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report not found"));
        // Check if the report is already resolved
        if (report.getStatus() != ReportState.PENDING) {
            throw new NotFoundException("Report already resolved");
        }
        // Update the report status
        report.setStatus(status);
        Reports updatedReport = reportRepository.save(report);
        //reportMapper.entityListToDTOList(updatedReport);
        return new ReportDTO(
                updatedReport.getReportId(),
                updatedReport.getReporter().getUserId(),
                updatedReport.getReportedUser().getUserId(),
                updatedReport.getReportType(),
                updatedReport.getFeedback(),
                updatedReport.getReportDate(),
                updatedReport.getStatus()
        );
    }

    @Override
    public PaginatedReportDTO getAllReports(int page, int size) {
        Page<Reports> reports = reportRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
        if (reports.getSize() > 0) {
            List<ReportDTO> reported = reports.stream()
                    .map(report -> new ReportDTO(
                    report.getReportId(),
                    report.getReporter().getUserId(),
                    report.getReportedUser().getUserId(),
                    report.getReportType(),
                    report.getFeedback(),
                    report.getReportDate(),
                    report.getStatus()
            )).collect(Collectors.toList());
            return new PaginatedReportDTO(reported,null,reportRepository.countAllBy());
        }
        return null;
    }

//    wrong name, this gives the reports by reported user id
    @Override
    public PaginatedReportDTO getAllReportsById(Long userId,int page, int size) {
        // Check if the reported user exists
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Reported user not found");
        }

       int totalPenaltyScore = reportRepository.getSumByReportedUser_UserId(userId) == null ? 0 : reportRepository.getSumByReportedUser_UserId(userId);
        // Calculate the total penalty score for the reported user

        Page<Reports> reports = reportRepository.findAllByReportedUser_UserId(userId,PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));

        if (reports.getSize() > 0) {
            List<ReportDTO> reported = reports.stream()
                    .map(report -> new ReportDTO(
                            report.getReportId(),
                            report.getReporter().getUserId(),
                            report.getReportedUser().getUserId(),
                            report.getReportType(),
                            report.getFeedback(),
                            report.getReportDate(),
                            report.getStatus()
                    )).collect(Collectors.toList());
            return new PaginatedReportDTO(reported, (long) totalPenaltyScore,(long) reportRepository.countByReportedUser_UserId(userId));
        }
        return null;
    }
}
