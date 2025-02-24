package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceIMPL implements AdminService {
    private final JobRepo jobRepository;
    private final ApplicationRepo applicationRepository;
    private final UserRepo userRepository;

    public AdminStatsResponseDTO getPlatformStatistics() {
        AdminStatsResponseDTO response = new AdminStatsResponseDTO();

        // Total jobs posted
        response.setTotalJobsPosted((int) jobRepository.count());

        // Jobs by category
        Map<String, Integer> jobsByCategory = jobRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(job -> job.getJobCategory().toString(), Collectors.summingInt(job -> 1)));
        response.setJobsByCategory(jobsByCategory);

        // Most applied job
        response.setMostAppliedJob(jobRepository.findMostAppliedJob());

        // Least applied job
        response.setLeastAppliedJob(jobRepository.findLeastAppliedJob());

        // Top employer (by job count)
        response.setTopEmployer(userRepository.findTopEmployer());

        // Most active student (by applications)
        response.setMostActiveStudent(userRepository.findMostActiveStudent());

        // Jobs by location
        Map<String, Integer> jobsByLocation = jobRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        job -> job.getJobLocations().toString(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue) // Convert Long to Integer
                ));
        response.setJobsByLocation(jobsByLocation);

        return response;
    }
}
