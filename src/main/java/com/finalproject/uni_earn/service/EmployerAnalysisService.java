package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedCategoryStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobSummeryDTO;
import com.finalproject.uni_earn.entity.enums.JobStatus;

import java.util.Date;

public interface EmployerAnalysisService {

    PaginatedJobSummeryDTO getAllJobSummeryForEmployer(long employerId, int page, int size);
    PaginatedJobSummeryDTO getJobsByEmployerAndDateRange(long employerId, Date startDate, Date endDate, int page, int size);
    PaginatedJobSummeryDTO getJobsByEmployerAndStatus(long employerId, JobStatus jobStatus, int page, int size);
    PaginatedJobSummeryDTO getJobsByEmployerStatusAndDateRange(long employerId, JobStatus jobStatus, Date startDate, Date endDate, int page, int size);

    // New methods
    PaginatedJobStaticsDTO getJobsWithMostApplicationsByEmployerId(long employerId, int page, int size);
    PaginatedJobStaticsDTO getJobsWithLeastApplicationsByEmployerId(long employerId, int page, int size);
    PaginatedCategoryStaticsDTO getMostPopularJobCategoriesByEmployerId(long employerId, int page, int size);

}
