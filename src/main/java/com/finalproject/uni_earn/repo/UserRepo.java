package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer>{
    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
}
