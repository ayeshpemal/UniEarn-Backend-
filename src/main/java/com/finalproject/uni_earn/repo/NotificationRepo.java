package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

}
