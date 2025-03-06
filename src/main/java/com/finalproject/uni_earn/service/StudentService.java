package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;

public interface StudentService {
    PaginatedUserResponseDTO getAllStudents(int page);
}
