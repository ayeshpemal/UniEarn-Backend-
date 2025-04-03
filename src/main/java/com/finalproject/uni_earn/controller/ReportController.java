package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.dto.request.ReportRequestDTO;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.service.ReportService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//this contains admin functions , not for the user
@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/report/")
public class ReportController {
    final private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PostMapping(value = "/submit")
    public ResponseEntity<StandardResponse> submitReport(@RequestBody ReportRequestDTO reportDTO) {
        String report = reportService.submitReport(reportDTO);
        return ResponseEntity.ok(new StandardResponse(201, "Success", report));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/getall", params = {"page", "size"})
    public ResponseEntity<StandardResponse> getAllReports(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        PaginatedReportDTO paginatedreportdto = reportService.getAllReports(page, size);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", paginatedreportdto));
    }

    //to get the id of the reported user
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/getall-by-id", params = {"userId","page", "size"})
    public ResponseEntity<StandardResponse> getAllReportsByCount(@RequestParam(value = "userId") Long userId, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        PaginatedReportDTO paginatedreportdto = reportService.getAllReportsById(userId,page, size);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", paginatedreportdto));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/resolve", params = {"reportId", "status"})
    public ResponseEntity<StandardResponse> resolveReport(@RequestParam(value = "reportId") Long reportId, @RequestParam(value = "status") ReportState status) {
        ReportDTO updatedReportDTO = reportService.resolveReport(reportId, status);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", updatedReportDTO));
    }

}
