package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Reports;
import com.finalproject.uni_earn.entity.enums.ReportState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepo extends JpaRepository<Reports, Long> {

    Page<Reports> findAll(Pageable pageable);
    Page<Reports> findAllByReportedUser_UserId(int id,Pageable pageable);
    long countAllBy();

}
