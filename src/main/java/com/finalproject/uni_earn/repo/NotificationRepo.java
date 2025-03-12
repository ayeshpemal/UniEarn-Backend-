package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.JobNotification;
import com.finalproject.uni_earn.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface NotificationRepo extends JpaRepository<JobNotification, Long> {

    Page<JobNotification> findAllByRecipient(User recipient, Pageable pageable);

    Long countByRecipient(User user);
}
