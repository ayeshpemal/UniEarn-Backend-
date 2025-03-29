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

import java.time.LocalDateTime;
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

    @Query(value = "SELECT u.user_id AS userId, u.user_name AS userName, u.email AS email, u.role AS role " +
            "FROM users u " +
            "ORDER BY (SELECT COUNT(j.job_id) FROM jobs j " +
            "WHERE j.employer_id = u.user_id AND j.created_at BETWEEN :startDate AND :endDate) DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Tuple> findTopEmployerByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query(value = """
    SELECT u.user_id AS userId, u.user_name AS userName, u.email AS email, u.role AS role 
    FROM users u 
    ORDER BY (
        (SELECT COUNT(a.application_id) 
         FROM application a 
         WHERE a.student_id = u.user_id 
         AND a.applied_date BETWEEN :startDate AND :endDate)
        + 
        (SELECT COUNT(a2.application_id) 
         FROM application a2 
         WHERE a2.team_id IN 
            (SELECT tm.team_id FROM team_members tm WHERE tm.student_id = u.user_id) 
         AND a2.applied_date BETWEEN :startDate AND :endDate)
    ) DESC
    LIMIT 1
    """, nativeQuery = true)
    Optional<Tuple> findMostActiveStudentByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<User> findByRole(Role role);

    Optional<User> findByEmailAndRole(String defaultAdminEmail, Role role);

    Optional<User> findByUserId(Long userId);
}
