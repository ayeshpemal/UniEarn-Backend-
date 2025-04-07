package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedEmployerResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.service.EmployerService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/applications/select/{applicationId}")
    public ResponseEntity<StandardResponse> selectCandidate(@PathVariable int applicationId) {
        String response = employerService.selectCandidate(applicationId); // Call service method
        return ResponseEntity.ok(new StandardResponse(200,"Success",response)); // Return the result
    }

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllEmployers(@RequestParam(defaultValue = "0") int page) {
        PaginatedUserResponseDTO responseDTO = employerService.getAllEmployers(page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<StandardResponse> searchEmployers(@RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        PaginatedEmployerResponseDTO responseDTO = employerService.searchEmployers(query, page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping("/search/follow")
    public ResponseEntity<StandardResponse> searchEmployersWithFollowStatus(@RequestParam Long userId, @RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        PaginatedEmployerResponseDTO responseDTO = employerService.searchEmployersWithFollowStatus(userId, query, page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }
}
