package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.entity.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface EmployerRepo extends JpaRepository<Employer, Long> {
    Employer getEmployerByUserId(Long employerId);
    Page<Employer> findByCompanyNameContainingIgnoreCase(String searchTerm, Pageable pageable);
    Long countByCompanyNameContainingIgnoreCase(String searchTerm);
}
