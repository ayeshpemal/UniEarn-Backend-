package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface JobRepo extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {

    Job getJobByJobId(Long jobId);
    boolean existsByJobId(Long jobId);

    Page<Job> findAllByJobCategory(JobCategory jobCategory, Pageable pageable);
    Page<Job> findAllByEmployer(Employer employer, Pageable pageable);
    Page<Job> findAllByJobLocationsContaining(Location location, Pageable pageable);
    Page<Job> findAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList, Pageable pageable);
    Page<Job> findAllByJobDescriptionContaining(String keyword, Pageable pageable);

    Integer countAllByJobCategory(JobCategory jobCategory);
    Integer countAllByJobLocationsContaining(Location location);
    Integer countAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList);
    Integer countAllByJobDescriptionContaining(String keyword);

    @Query(value = "UPDATE Jobs SET activeStatus = ?2 WHERE job_id = ?1",nativeQuery = true)
    void setActiveState(Long jobId, boolean status);

}
