package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.repo.ReportRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ReportService;
import com.finalproject.uni_earn.util.mappers.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceIMPL implements ReportService {
    // remember about int reporterId, int reportedUserId,  , becuase it is LONG on the Report entity , Think it is better user ids are Long not int
    private ReportRepo reportRepository;
    private UserRepo userRepository;
    private ReportMapper reportMapper;

    @Autowired
    public ReportServiceIMPL(ReportRepo reportRepository, UserRepo userRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
    }


    @Override
    public Reports submitReport(long reporterId, long reportedUserId, String feedback) {
        Reports report = new Reports();
        report.setReporter(userRepository.findById(reporterId).orElseThrow());
        report.setReportedUser(userRepository.findById(reportedUserId).orElseThrow());
        report.setFeedback(feedback);
        report.setReportDate(LocalDateTime.now());
        report.setStatus(ReportState.PENDING);
        reportRepository.save(report);
//        return reportRepository.save(report);
        return null;
    }

    // Here we take input as int ,due to user table have int as id, but in report table it is long
    @Override
    public ReportDTO resolveReport(Long reportId, ReportState status) {
        Reports report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus(status);
        Reports updatedReport = reportRepository.save(report);
        ReportDTO updatedReportdDTO = reportMapper.entityListToDTOList(updatedReport);

        return updatedReportdDTO;
    }

    @Override
    public PaginatedReportDTO getallReports(int page, int size) {
        Page<Reports> reports = reportRepository.findAll(PageRequest.of(page, size));
        if (reports.getSize() > 0) {
            List<ReportDTO> reportdtos = reportMapper.entityListToDTOList(reports);
            PaginatedReportDTO paginatedReportDTO = new PaginatedReportDTO(reportdtos,reportRepository.countAllBy());
            return paginatedReportDTO;
        }
        return null;
    }

//    wrong name, this gives the reports by reported user id
    @Override
    public PaginatedReportDTO getallReportsByCount(int id,int page, int size) {
        Page<Reports> reports = reportRepository.findAllByReportedUser_UserId(id,PageRequest.of(page, size));
        if (reports.getSize() > 0) {
            List<ReportDTO> reportdtos = reportMapper.entityListToDTOList(reports);
            PaginatedReportDTO paginatedReportDTO = new PaginatedReportDTO(reportdtos,reportRepository.countAllBy());
            return paginatedReportDTO;
        }
        return null;
    }
}
