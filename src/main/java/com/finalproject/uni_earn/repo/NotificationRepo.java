package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface NotificationRepo extends JpaRepository<Notification, Long> {

}
