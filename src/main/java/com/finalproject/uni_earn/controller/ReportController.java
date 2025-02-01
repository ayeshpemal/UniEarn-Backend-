package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedReportDTO;
import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.service.impl.ReportServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//this contains admin functions , not for the user
@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/report/")
public class ReportController {
    private ReportServiceIMPL reportServiceIMPL;

    @Autowired
    public ReportController(ReportServiceIMPL reportServiceIMPL) {
        this.reportServiceIMPL = reportServiceIMPL;
    }

    @GetMapping(value = "/getall", params = {"page", "size"})
    public ResponseEntity<StandardResponse> getAllReports(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        PaginatedReportDTO paginatedreportdto = reportServiceIMPL.getallReports(page, size);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", paginatedreportdto));
    }
//to get the id of the reported user
    @GetMapping(value = "/getall-by-id", params = {"Id,page", "size"})
    public ResponseEntity<StandardResponse> getAllReportsByCount(@RequestParam(value = "ID") int id, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        PaginatedReportDTO paginatedreportdto = reportServiceIMPL.getallReportsByCount(id,page, size);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", paginatedreportdto));
    }

    @PutMapping(value = "/resolve", params = {"reportId", "status"})
    public ResponseEntity<StandardResponse> resolveReport(@RequestParam(value = "reportId") Long reportId, @RequestParam(value = "status") ReportState status) {
        ReportDTO updatedReportDTO = reportServiceIMPL.resolveReport(reportId, status);
        return (ResponseEntity<StandardResponse>) ResponseEntity.ok(new StandardResponse(200, "Success", updatedReportDTO));
    }

}
