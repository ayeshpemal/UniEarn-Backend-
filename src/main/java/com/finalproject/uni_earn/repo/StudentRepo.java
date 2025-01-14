package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface StudentRepo extends JpaRepository<Student, Long> {
    List<Student> findAllByPreferencesContaining(JobCategory jobCategory);
    Student getStudentByUserId(Long studentId);
}
