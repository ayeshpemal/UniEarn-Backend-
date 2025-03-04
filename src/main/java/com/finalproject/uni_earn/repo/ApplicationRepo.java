package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


    List<Application> findByStudent(Student student);

    long countByStudent(Student student);
}
