package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.entity.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface EmployerRepo extends JpaRepository<Employer, Long> {
    Employer getEmployerByUserId(Long employerId);
    @Query("SELECT e FROM Employer e WHERE (:searchTerm IS NULL OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND e.isDeleted = false")
    Page<Employer> findEmployers(@Param("searchTerm") String searchTerm, Pageable pageable);
    @Query("SELECT COUNT(e) FROM Employer e WHERE (:searchTerm IS NULL OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND e.isDeleted = false")
    Long countEmployers(@Param("searchTerm") String searchTerm);
}
