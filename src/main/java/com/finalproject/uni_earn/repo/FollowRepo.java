package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Follow;
import com.finalproject.uni_earn.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Repository
@EnableJpaRepositories
public interface FollowRepo extends JpaRepository<Follow,Long> {
    void delete(Follow follow);

    Optional<Object> findByStudentAndEmployer(Student student, Employer employer);


    List<Follow> findAllByEmployer(Employer employer);

    @Query("SELECT f.employer FROM Follow f WHERE f.student.userId = :studentId")
    Page<Employer> findFollowingEmployersByStudentId(Long studentId, Pageable pageable);
    boolean existsByStudentAndEmployer(Student student, Employer employer);
}
