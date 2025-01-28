package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface JobRepo extends JpaRepository<Job,Long> {
    
    Job getJobByJobId(Long jobId);
    boolean existsByJobId(Long jobId);
    Page<Job> findAllByJobCategory(JobCategory jobCategory, Pageable pageable);
    Integer countAllByJobCategory(JobCategory jobCategory);
    Page<Job> findAllByEmployer(Employer employer, Pageable pageable);
    Integer countAllByEmployer(Employer employer);
    Page<Job> findAllByJobLocationsContaining(Location location, Pageable pageable);
    Integer countAllByJobLocationsContaining(Location location);
    Page<Job> findAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList, Pageable pageable);
    Integer countAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList);

    @Query(value = "UPDATE Job SET activeStatus = FALSE WHERE job_id = ?1",nativeQuery = true)
    void setActiveState(Long jobId);
    Optional<Job> findAllByEmployer(Employer employer);
}
