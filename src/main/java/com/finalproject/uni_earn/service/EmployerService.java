package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;

public interface EmployerService {


    String selectCandidate(long applicationId);

    PaginatedUserResponseDTO getAllEmployers(int page);
}