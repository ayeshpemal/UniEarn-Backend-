package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Integer> {

    Job getJobByJobId(Integer jobId);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
    List<Job> findAllByEmployer(Employer employer);
    List<Job> findAllByJobLocation(Location location);
    //List<Job> findAllByJobLocationAndCategory(Location location, JobCategory jobCategory);
}
