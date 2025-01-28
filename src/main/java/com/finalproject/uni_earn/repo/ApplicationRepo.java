package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Long> {
    static void Save(Application map) {
    }

    Page<Application> getAllByStudent(Student student, Pageable pageable);

    boolean existsByStudent_UserIdAndJob_JobIdAndStatus(Long userId, Long jobId, ApplicationStatus status);

    Application findByStudent_UserIdAndJob_JobId(Long userId, Long jobId);
}
