package com.finalproject.uni_earn.specification;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> filterJobs(Location location, List<JobCategory> categories, String keyword,
                                                Date startDateFrom, Date startDateTo) {
        return (Root<Job> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction(); // Default to TRUE (no filters applied)

            // Filter by single Location if provided
            if (location != null) {
                Join<Job, Location> locationJoin = root.join("jobLocations"); // Join because jobLocations is a List
                predicate = cb.and(predicate, cb.equal(locationJoin, location));
            }

            // Filter by multiple Categories if provided
            if (categories != null && !categories.isEmpty()) {
                predicate = cb.and(predicate, root.get("jobCategory").in(categories));
            }

            // Filter by keyword in Job Description if provided
            if (keyword != null && !keyword.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("jobDescription")), "%" + keyword.toLowerCase() + "%")
                );
            }

            // Filter by JobStatus = PENDING
            predicate = cb.and(predicate, cb.equal(root.get("jobStatus"), JobStatus.PENDING));

            // Filter by Start Date range if provided - comparing only the date part
            if (startDateFrom != null || startDateTo != null) {
                Expression<Date> jobStartDate = cb.function("DATE", Date.class, root.get("startDate"));

                if (startDateFrom != null && startDateTo != null) {
                    // Both dates provided - filter for range
                    Expression<Date> fromDate = cb.function("DATE", Date.class, cb.literal(startDateFrom));
                    Expression<Date> toDate = cb.function("DATE", Date.class, cb.literal(startDateTo));
                    predicate = cb.and(predicate,
                            cb.greaterThanOrEqualTo(jobStartDate, fromDate),
                            cb.lessThanOrEqualTo(jobStartDate, toDate));
                } else if (startDateFrom != null) {
                    // Only start date provided
                    Expression<Date> fromDate = cb.function("DATE", Date.class, cb.literal(startDateFrom));
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(jobStartDate, fromDate));
                } else {
                    // Only end date provided
                    Expression<Date> toDate = cb.function("DATE", Date.class, cb.literal(startDateTo));
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(jobStartDate, toDate));
                }
            }

            return predicate;
        };
    }
}
