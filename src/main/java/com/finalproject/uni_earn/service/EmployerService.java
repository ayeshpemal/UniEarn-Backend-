package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedEmployerResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;

public interface EmployerService {


    String selectCandidate(long applicationId);

    PaginatedUserResponseDTO getAllEmployers(int page);

    PaginatedEmployerResponseDTO searchEmployers(String query, int page);

    PaginatedEmployerResponseDTO searchEmployersWithFollowStatus(Long userId, String query, int page);
}