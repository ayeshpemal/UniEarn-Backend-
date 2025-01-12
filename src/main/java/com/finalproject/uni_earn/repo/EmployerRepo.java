package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerRepo extends JpaRepository<Employer, Long> {

}
