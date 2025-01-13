package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Job, Long> {

}
