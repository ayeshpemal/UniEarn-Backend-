package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Follow;
import com.finalproject.uni_earn.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepo extends JpaRepository<Follow,Long> {
    void delete(Follow follow);

    Optional<Object> findByStudentAndEmployer(Student student, Employer employer);


}
