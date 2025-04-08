package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.service.StudentService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllStudents(@RequestParam(defaultValue = "0") int page) {
        PaginatedUserResponseDTO responseDTO = studentService.getAllStudents(page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<StandardResponse> searchStudents(@RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        PaginatedStudentResponseDTO responseDTO = studentService.searchStudents(query, page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }

    @PreAuthorize("hasRole('STUDENT') or  hasRole('ADMIN')")
    @GetMapping("/search-with-follow-status")
    public ResponseEntity<StandardResponse> searchStudentsWithFollowStatus(@RequestParam Long userId, @RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        PaginatedStudentResponseDTO responseDTO = studentService.searchStudentsWithFollowStatus(userId, query, page);
        return ResponseEntity.ok(new StandardResponse(200, "Success", responseDTO));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/applications/confirm")
    public ResponseEntity<StandardResponse> applicationConfirm(@RequestParam Long applicationId, @RequestParam Long studentId) {
        String response = studentService.applicationConfirm(applicationId, studentId); // Call service method
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", response),
                HttpStatus.OK // Return the result
        );
    }
}
