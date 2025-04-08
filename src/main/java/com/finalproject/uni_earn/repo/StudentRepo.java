package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface StudentRepo extends JpaRepository<Student, Long> {
    List<Student> findAllByPreferencesContaining(JobCategory jobCategory);
    Student getStudentByUserId(Long studentId);

    @Query("SELECT s FROM Student s JOIN s.followers f WHERE f.userId = :studentId")
    Page<Student> findFollowingByStudentId(Long studentId, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE (:searchTerm IS NULL OR LOWER(s.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.isDeleted = false")
    Page<Student> findByDisplayNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s.preferences FROM Student s WHERE s.userId = :studentId")
    List<JobCategory> findPreferencesByStudentId(Long studentId);

    @Query("SELECT COUNT(s) FROM Student s WHERE (:searchTerm IS NULL OR LOWER(s.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.isDeleted = false")
    Long countByDisplayNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

}
