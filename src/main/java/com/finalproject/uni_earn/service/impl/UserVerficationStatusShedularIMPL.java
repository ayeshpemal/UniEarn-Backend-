package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
public class UserVerficationStatusShedularIMPL {
    private final UserRepo userRepo;

    @Autowired
    public UserVerficationStatusShedularIMPL(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Scheduled(cron = "0 0 6 * * ?")
    public void deleteUnverifiedUsers() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        List<User> usersToDelete = userRepo.findByVerifiedFalseAndCreatedAtBefore(cutoffTime);

        userRepo.deleteAll(usersToDelete);
    }
}
