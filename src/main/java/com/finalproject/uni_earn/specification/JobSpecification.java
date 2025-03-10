package com.finalproject.uni_earn.specification;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JobSpecification {

    public static Specification<Job> filterJobs(Location location, List<JobCategory> categories, String keyword) {
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
                predicate = cb.and(predicate, cb.like(root.get("jobDescription"), "%" + keyword + "%"));
            }

            // Filter by JobStatus = PENDING
            predicate = cb.and(predicate, cb.equal(root.get("jobStatus"), JobStatus.PENDING));

            return predicate;
        };
    }
}
