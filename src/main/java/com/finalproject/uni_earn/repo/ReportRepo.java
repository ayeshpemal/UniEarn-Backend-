package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepo extends JpaRepository<Reports, Long> {

    Page<Reports> findAll(Pageable pageable);
    Page<Reports> findAllByReportedUser_UserId(Long id,Pageable pageable);
    long countAllBy();

    @Query("SELECT COUNT(r) FROM Reports r " +
            "WHERE r.reportedUser.userId = :userId " +
            "AND r.reportDate > :after")
    long countByReportedUserIdAndReportDateAfter(
            Long userId,
            LocalDateTime after
    );

    @Query("SELECT SUM(r.penaltyScore) FROM Reports r " +
            "WHERE r.reportedUser.userId = :userId " +
            "AND r.reportDate > :after")
    double sumPenaltyScoreByReportedUserIdAndReportDateAfter(
            Long userId,
            LocalDateTime after
    );
}
