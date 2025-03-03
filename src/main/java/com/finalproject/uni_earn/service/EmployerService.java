package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedEmployerResponseDTO;
import com.finalproject.uni_earn.entity.Job;

public interface EmployerService {


    String selectCandidate(long applicationId);

    PaginatedEmployerResponseDTO getAllEmployers(int page);
}