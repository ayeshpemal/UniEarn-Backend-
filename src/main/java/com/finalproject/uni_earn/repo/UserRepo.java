package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.Role;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepo extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    Optional<User> findByVerificationToken(String token);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findAllActiveUsers();

    Optional<User> findByUserNameAndIsDeletedFalse(String username);
    boolean existsByUserId(Long userId);

    @Query("select u.role from User u where u.userId = :userId")
    Optional <String> findRoleByUserId(@Param("userId") Long userId);

    // Top employer (most jobs posted)
    @Query(value = "SELECT u.user_id AS userId, u.user_name AS userName, u.email as email, u.role as role FROM users u ORDER BY (SELECT COUNT(j.job_id) FROM jobs j WHERE j.employer_id = u.user_id) DESC LIMIT 1", nativeQuery = true)
    Optional<Tuple> findTopEmployer();

    // Most active student (most applications submitted)
    @Query(value = "SELECT u.user_id AS userId, u.user_name AS userName, u.email as email, u.role as role FROM users u ORDER BY (SELECT COUNT(a.application_id) FROM application a WHERE a.student_id = u.user_id) DESC LIMIT 1", nativeQuery = true)
    Optional<Tuple> findMostActiveStudent();


    List<User> findByRole(Role role);
}
