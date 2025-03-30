package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.Response.JobCategoryStatisticsDTO;
import com.finalproject.uni_earn.dto.Response.JobStaticsDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.Team;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepo extends JpaRepository<Application, Long> {
    static void Save(Application map) {
    }

    Page<Application> getAllByStudent(Student student, Pageable pageable);

    boolean existsByStudent_UserIdAndJob_JobIdAndStatus(Long userId, Long jobId, ApplicationStatus status);

    Application findByStudent_UserIdAndJob_JobId(Long userId, Long jobId);

    boolean existsByJob_JobIdAndTeam_Id(Long jobId, Long teamId);

    boolean existsByJob_JobIdAndStudent_UserId(Long jobId, Long studentId);

    // ✅ Count total applications for a student
    @Query("SELECT COUNT(a) FROM Application a WHERE a.student.userId = :studentId")
    int countByStudentId(@Param("studentId") Long studentId);

    // ✅ Count applications grouped by status for a student
    @Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.student.userId = :studentId GROUP BY a.status")
    List<Object[]> countApplicationsByStatus(@Param("studentId") Long studentId);

    // ✅ Count total applications for a job
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.jobId = :jobId")
    int countByJobId(@Param("jobId") Long jobId);

    // ✅ Find the most applied job
    @Query(value = "SELECT j.job_title FROM application a JOIN job j ON a.job_id = j.id GROUP BY j.id ORDER BY COUNT(a.job_id) DESC LIMIT 1", nativeQuery = true)
    String findMostAppliedJob();

    // ✅ Find the least applied job
    @Query(value = "SELECT j.job_title FROM application a JOIN job j ON a.job_id = j.id GROUP BY j.id ORDER BY COUNT(a.job_id) ASC LIMIT 1", nativeQuery = true)
    String findLeastAppliedJob();


    List<Application> findByStudentAndCreatedAtBetween(Student student, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM Application a WHERE a.job.jobId = :jobId AND a.team IS NOT NULL AND a.status = 'PENDING'")
    List<Application> findPendingGroupApplicationsByJobId(@Param("jobId") Long jobId);

    @Query("SELECT a FROM Application a WHERE a.job.jobId = :jobId AND a.student IS NOT NULL AND a.status = 'PENDING'")
    List<Application> findPendingStudentApplicationsByJobId(@Param("jobId") Long jobId);

    long countByStudent(Student student);

    // Check if a team has applied for the job
    @Query("SELECT COUNT(a) > 0 FROM Application a JOIN a.team t JOIN t.members m WHERE a.job.jobId = :jobId AND m.userId = :studentId")
    boolean isStudentInAppliedTeam(@Param("jobId") Long jobId, @Param("studentId") Long studentId);

    @Query("SELECT a FROM Application a JOIN a.team t JOIN t.members m WHERE a.job.jobId = :jobId AND m.userId = :studentId")
    Application findStudentInAppliedTeam(@Param("jobId") Long jobId, @Param("studentId") Long studentId);

    // Get applications where student is in an applied team (Paginated)
    @Query("SELECT a FROM Application a JOIN a.team t JOIN t.members m WHERE m.userId = :studentId")
    Page<Application> findApplicationsByStudentInTeam(@Param("studentId") Long studentId, Pageable pageable);

    // Get total count of applications where student is in a team
    @Query("SELECT COUNT(a) FROM Application a JOIN a.team t JOIN t.members m WHERE m.userId = :studentId")
    long countApplicationsByStudentInTeam(@Param("studentId") Long studentId);

    Optional<Application> findByTeamAndStatus(Team team, ApplicationStatus applicationStatus);

    Application findByJob_JobIdAndStudent_UserId(Long jobId, Long studentId);

    @Query("SELECT a FROM Application a WHERE a.team.id IN " +
            "(SELECT t.id FROM Team t JOIN t.members m WHERE m.userId = :studentId) " +
            "AND a.appliedDate BETWEEN :startDate AND :endDate")
    List<Application> findByStudentInTeamAndDateRange(@Param("studentId") Long studentId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);



    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobStaticsDTO(" +
            "j.jobId, COUNT(a), j.jobCategory, j.startDate, j.endDate) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = 'PENDING' " +
            "GROUP BY j.jobId, j.jobCategory, j.startDate, j.endDate " +
            "ORDER BY COUNT(a) DESC")
    Page<JobStaticsDTO> findJobsWithMostApplicationsByEmployerId(
            @Param("employerId") Long employerId,
            Pageable pageable);


    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobStaticsDTO(" +
            "j.jobId, COUNT(a), j.jobCategory, j.startDate, j.endDate) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = 'PENDING' " +
            "GROUP BY j.jobId, j.jobCategory, j.startDate, j.endDate " +
            "ORDER BY COUNT(a) ASC")
    Page<JobStaticsDTO> findJobsWithLeastApplicationsByEmployerId(
            @Param("employerId") Long employerId,
            Pageable pageable);

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.JobCategoryStatisticsDTO(" +
            "j.jobCategory, COUNT(a)) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "GROUP BY j.jobCategory " +
            "ORDER BY COUNT(a) DESC")
    Page<JobCategoryStatisticsDTO> findMostPopularJobCategoriesByEmployerId(
            @Param("employerId") Long employerId,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT j.jobId) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "AND j.jobStatus = 'PENDING'")
    long countJobsWithApplicationsByEmployerId(@Param("employerId") Long employerId);


    @Query("SELECT COUNT(DISTINCT j.jobCategory) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId")
    long countJobCategoriesWithApplicationsByEmployerId(@Param("employerId") Long employerId);

    // Get top 3 jobs with most applications for an employer
    @Query("SELECT j.jobId, j.jobTitle, COUNT(a) as appCount " +
            "FROM Application a JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "GROUP BY j.jobId, j.jobTitle " +
            "ORDER BY COUNT(a) DESC " +
            "LIMIT 3")
    List<Object[]> findJobsWithMostApplicationsByEmployerId(@Param("employerId") Long employerId);

    // Get top 3 jobs with least applications for an employer
    @Query("SELECT j.jobId, j.jobTitle, COUNT(a) as appCount " +
            "FROM Application a JOIN a.job j " +
            "WHERE j.employer.userId = :employerId " +
            "GROUP BY j.jobId, j.jobTitle " +
            "ORDER BY COUNT(a) ASC " +
            "LIMIT 3")
    List<Object[]> findJobsWithLeastApplicationsByEmployerId(@Param("employerId") Long employerId);


}
