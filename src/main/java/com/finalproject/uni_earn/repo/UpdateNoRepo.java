package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.UpdateNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateNoRepo extends JpaRepository<UpdateNotification,Long> {
}
