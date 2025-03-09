package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;

public interface StudentService {
    PaginatedUserResponseDTO getAllStudents(int page);

    PaginatedStudentResponseDTO searchStudents(String query, int page);

    PaginatedStudentResponseDTO searchStudentsWithFollowStatus(Long userId, String query, int page);

    String applicationConfirm(Long applicationId);
}
