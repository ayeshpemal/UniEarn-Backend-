package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.service.AdminService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value="/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<StandardResponse> getAdminStats() {
        AdminStatsResponseDTO stats = adminService.getPlatformStatistics();
        return new ResponseEntity<>(
                new StandardResponse(200, "Admin statistics fetched successfully.", stats),
                HttpStatus.OK
        );
    }
}
