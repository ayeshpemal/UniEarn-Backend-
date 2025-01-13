package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface JobRepo extends JpaRepository<Job,Long> {

    Job getJobByJobId(Long jobId);
    boolean existsByJobId(Long jobId);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
    List<Job> findAllByEmployer(Employer employer);
    List<Job> findAllByJobLocationsContaining(Location location);
    List<Job> findAllByJobLocationsContainingAndJobCategoryEquals(Location location, JobCategory jobCategory);
    List<Job> findAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList);

    @Query(value = "UPDATE Job SET activeStatus = FALSE WHERE job_id = ?1",nativeQuery = true)
    void setActiveState(Long jobId);
}
