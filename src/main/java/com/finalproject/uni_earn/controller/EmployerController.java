package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.service.EmployerService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    //@PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/applications/select/{applicationId}")
    public ResponseEntity<String> selectCandidate(@PathVariable int applicationId) {
        String response = employerService.selectCandidate(applicationId); // Call service method
        return ResponseEntity.ok(response); // Return the result
    }

    //@PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllEmployers(@RequestParam(defaultValue = "0") int page) {
        PaginatedUserResponseDTO responseDTO = employerService.getAllEmployers(page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }
}
