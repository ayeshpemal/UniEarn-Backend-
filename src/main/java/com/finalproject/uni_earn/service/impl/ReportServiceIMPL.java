package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.dto.request.ReportRequestDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.repo.ReportRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ReportProcessService;
import com.finalproject.uni_earn.service.ReportService;
import com.finalproject.uni_earn.util.mappers.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceIMPL implements ReportService {
    // remember about int reporterId, int reportedUserId, because it is LONG on the Report entity , Think it is better user ids are Long not int
    final private ReportRepo reportRepository;
    final private UserRepo userRepository;
    final private ReportMapper reportMapper;
    final private ReportProcessService reportProcessService;

    @Autowired
    public ReportServiceIMPL(ReportRepo reportRepository, UserRepo userRepository, ReportMapper reportMapper, ReportProcessService reportProcessService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
        this.reportProcessService = reportProcessService;
    }


    @Override
    public Reports submitReport(ReportRequestDTO reportRequestDTO) {
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
        return  reportRepository.save(report);

    }

    // Here we take input as int ,due to user table have int as id, but in report table it is long
    @Override
    public ReportDTO resolveReport(Long reportId, ReportState status) {
        Reports report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus(status);
        Reports updatedReport = reportRepository.save(report);
        return reportMapper.entityListToDTOList(updatedReport);
    }

    @Override
    public PaginatedReportDTO getAllReports(int page, int size) {
        Page<Reports> reports = reportRepository.findAll(PageRequest.of(page, size));
        if (reports.getSize() > 0) {
            List<ReportDTO> reported = reportMapper.entityListToDTOList(reports);
            return new PaginatedReportDTO(reported,reportRepository.countAllBy());
        }
        return null;
    }

//    wrong name, this gives the reports by reported user id
    @Override
    public PaginatedReportDTO getAllReportsById(int id,int page, int size) {
        Page<Reports> reports = reportRepository.findAllByReportedUser_UserId(id,PageRequest.of(page, size));
        if (reports.getSize() > 0) {
            List<ReportDTO> reported = reportMapper.entityListToDTOList(reports);
            return new PaginatedReportDTO(reported,reportRepository.countAllBy());
        }
        return null;
    }
}
