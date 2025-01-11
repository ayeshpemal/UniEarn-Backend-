package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Integer> {
    @Query(value = "SELECT * FROM Job WHERE id=?1",nativeQuery = true)
    Job getJobByById(Integer jobId);

    @Query(value = "SELECT * FROM Job WHERE category=?1",nativeQuery = true)
    List<Job> filterjob(String category);
}
