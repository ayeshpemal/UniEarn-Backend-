package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.JobSummeryDetails;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface JobRepo extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {

    Job getJobByJobId(Long jobId);
    boolean existsByJobId(Long jobId);

    Page<Job> findAllByJobCategory(JobCategory jobCategory, Pageable pageable);
    Page<Job> findAllByEmployer(Employer employer, Pageable pageable);
    Page<Job> findAllByJobLocationsContaining(Location location, Pageable pageable);
    Page<Job> findAllByJobLocationsContainingAndJobCategoryInAndJobStatus(Location location, List<JobCategory> jobCategoryList, JobStatus jobStatus, Pageable pageable);
    Page<Job> findAllByJobDescriptionContaining(String keyword, Pageable pageable);

    Integer countAllByJobCategory(JobCategory jobCategory);
    Integer countAllByJobLocationsContaining(Location location);
    Integer countAllByJobLocationsContainingAndJobCategoryIn(Location location, List<JobCategory> jobCategoryList);
    Integer countAllByJobDescriptionContaining(String keyword);

    @Query(value = "UPDATE Jobs SET activeStatus = ?2 WHERE job_id = ?1",nativeQuery = true)
    void setActiveState(Long jobId, boolean status);

    @Query(value = "UPDATE Job SET activeStatus = FALSE WHERE job_id = ?1",nativeQuery = true)
    void setActiveState(Long jobId);

    Optional<Job> findAllByEmployer(Employer employer);

    boolean existsByJobIdAndJobStatusAndEmployer_UserId(Long jobId, JobStatus jobStatus, Long userId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt BETWEEN :startDate AND :endDate")
    long countJobsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT j.jobCategory, COUNT(j) FROM Job j WHERE j.createdAt BETWEEN :startDate AND :endDate GROUP BY j.jobCategory")
    List<Object[]> countJobsByCategory(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
    SELECT j FROM Job j 
    WHERE j.createdAt BETWEEN :startDate AND :endDate
""")
    List<Job> findJobsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT j.jobId FROM Application a JOIN a.job j WHERE j.createdAt BETWEEN :startDate AND :endDate GROUP BY j ORDER BY COUNT(a) DESC LIMIT 3")
    List<Long> findMostAppliedJobByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT j.jobId FROM Application a JOIN a.job j WHERE j.createdAt BETWEEN :startDate AND :endDate GROUP BY j ORDER BY COUNT(a) ASC LIMIT 3")
    List<Long> findLeastAppliedJobByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countByEmployer(Employer employer);

    List<Job> findByStartDateBeforeAndJobStatusNot(Date currentDate, JobStatus status);

    List<Job> findByEndDateBeforeAndJobStatusNot(Date currentDate, JobStatus status);

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobSummeryDetails(" +
            "j.jobId, j.jobTitle, j.jobDescription, j.startDate, j.endDate, j.startTime, j.endTime,j.jobStatus) " +
            "FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "ORDER BY j.startDate DESC")
    Page<JobSummeryDetails> getJobSummeryDetails(@Param("employerId") Long employerId, Pageable pageable);

    Long countByEmployer_UserId(Long employerId);

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobSummeryDetails(" +
            "j.jobId, j.jobTitle, j.jobDescription, j.startDate, j.endDate, j.startTime, j.endTime,j.jobStatus) " +
            "FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate " +
            "ORDER BY j.startDate DESC")
    Page<JobSummeryDetails> getJobsByEmployerAndDateRange(
            @Param("employerId") Long employerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate")
    Long countJobsByEmployerAndDateRange(
            @Param("employerId") Long employerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobSummeryDetails(" +
            "j.jobId, j.jobTitle, j.jobDescription, j.startDate, j.endDate, j.startTime, j.endTime, j.jobStatus) " +
            "FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = :jobStatus " +
            "ORDER BY j.startDate DESC")
    Page<JobSummeryDetails> getJobsByEmployerAndStatus(
            @Param("employerId") Long employerId,
            @Param("jobStatus") JobStatus jobStatus,
            Pageable pageable
    );
    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = :status")
    Long countJobsByEmployerAndStatus(
            @Param("employerId") Long employerId,
            @Param("status") JobStatus status
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobSummeryDetails(" +
            "j.jobId, j.jobTitle, j.jobDescription, j.startDate, j.endDate, j.startTime, j.endTime, j.jobStatus) " +
            "FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = :jobStatus " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate " +
            "ORDER BY j.startDate DESC")
    Page<JobSummeryDetails> getJobsByEmployerStatusAndDateRange(
            @Param("employerId") Long employerId,
            @Param("jobStatus") JobStatus jobStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = :jobStatus " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate")
    Long countJobsByEmployerStatusAndDateRange(
            @Param("employerId") Long employerId,
            @Param("jobStatus") JobStatus jobStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Count jobs by status for an employer
    @Query("SELECT COUNT(j) FROM Job j WHERE j.employer.userId = :employerId AND j.jobStatus = :status")
    Long countByEmployerIdAndStatus(@Param("employerId") Long employerId, @Param("status") JobStatus status);

    // Count jobs by category for an employer
    @Query("SELECT j.jobCategory, COUNT(j) FROM Job j WHERE j.employer.userId = :employerId GROUP BY j.jobCategory")
    List<Object[]> countByEmployerIdGroupByCategory(@Param("employerId") Long employerId);

    // Count jobs by location for an employer
    @Query("SELECT l, COUNT(j) FROM Job j JOIN j.jobLocations l WHERE j.employer.userId = :employerId GROUP BY l")
    List<Object[]> countByEmployerIdGroupByLocation(@Param("employerId") Long employerId);

    // Count jobs by employer and date range
    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate")
    Long countByEmployer_UserIdAndDateRange(
            @Param("employerId") Long employerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Count jobs by employer, status, and date range
    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = :status " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate")
    Long countByEmployerIdAndStatusAndDateRange(
            @Param("employerId") Long employerId,
            @Param("status") JobStatus status,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Count jobs by category for an employer with date range
    @Query("SELECT j.jobCategory, COUNT(j) FROM Job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate " +
            "GROUP BY j.jobCategory")
    List<Object[]> countByEmployerIdGroupByCategoryAndDateRange(
            @Param("employerId") Long employerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Count jobs by location for an employer with date range
    @Query("SELECT l, COUNT(j) FROM Job j " +
            "JOIN j.jobLocations l " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.startDate >= :startDate " +
            "AND j.endDate <= :endDate " +
            "GROUP BY l")
    List<Object[]> countByEmployerIdGroupByLocationAndDateRange(
            @Param("employerId") Long employerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );





}