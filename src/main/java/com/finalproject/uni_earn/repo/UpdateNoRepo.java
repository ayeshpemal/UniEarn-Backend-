package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.JobNotification;
import com.finalproject.uni_earn.entity.UpdateNotification;
import com.finalproject.uni_earn.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateNoRepo extends JpaRepository<UpdateNotification,Long> {
    Long countByRecipient(User user);

    Page<UpdateNotification> findAllByRecipient(User user, Pageable pageable);
}
