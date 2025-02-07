package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;

public interface NotificationService {

    String createFollowNotification(Employer employer, Job job);
}
