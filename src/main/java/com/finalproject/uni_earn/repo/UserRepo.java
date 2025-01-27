package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    boolean existsByUserId(Long userId);

    @Query("select u.role from User u where u.userId = :userId")
    Optional <String> findRoleByUserId(@Param("userId") Long userId);

}
