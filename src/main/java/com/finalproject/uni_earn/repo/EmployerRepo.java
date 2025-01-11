package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.request.EmployerRequestDto;
import com.finalproject.uni_earn.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerRepo extends JpaRepository<Employer, Long> {
    Employer save(EmployerRequestDto employer);

    List<Employer> findAll();

    Optional<Employer> findById(Long id);

    void deleteById(Long id);
}
