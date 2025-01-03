package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer>{
}
