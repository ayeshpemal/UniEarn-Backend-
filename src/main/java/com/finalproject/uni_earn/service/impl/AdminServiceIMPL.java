package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceIMPL implements AdminService {
    private final JobRepo jobRepository;
    private final ApplicationRepo applicationRepository;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public String makeUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new AlreadyExistException("User is already an admin.");
        }

        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return "User with ID " + userId + " is now an admin.";
    }

    /**
     * Remove admin privileges from a user.
     */
    @Transactional
    public String removeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new NotFoundException("User is not an admin.");
        }

        user.setRole(Role.STUDENT); // Defaulting back to a student, change if needed
        userRepository.save(user);
        return "User with ID " + userId + " is no longer an admin.";
    }

    /**
     * Get a list of all admins in the system.
     */
    public List<AdminResponseDTO> getAllAdmins() {
        List<User> user = userRepository.findByRole(Role.ADMIN);
        return user.stream()
                .map(u -> modelMapper.map(u, AdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    public AdminStatsResponseDTO getPlatformStatistics() {
        AdminStatsResponseDTO response = new AdminStatsResponseDTO();

        try {
            // Total jobs posted
            long jobCount = jobRepository.count();
            if (jobCount == 0) {
                throw new NotFoundException("No jobs found on the platform.");
            }
            response.setTotalJobsPosted((int) jobCount);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while counting jobs.", e);
        }

        try {
            // Jobs by category
            Map<String, Integer> jobsByCategory = jobRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(
                            job -> job.getJobCategory().toString(),
                            Collectors.summingInt(job -> 1)
                    ));
            response.setJobsByCategory(jobsByCategory.isEmpty() ? Collections.emptyMap() : jobsByCategory);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by category.", e);
        }

        try {
            // Most applied job
            response.setMostAppliedJob(jobRepository.findMostAppliedJob());
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the most applied job.", e);
        }

        try {
            // Least applied job
            response.setLeastAppliedJob(jobRepository.findLeastAppliedJob());
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the least applied job.", e);
        }

        try {
            // Top employer (by job count)
            String topEmployer = userRepository.findTopEmployer();
            response.setTopEmployer(topEmployer != null ? topEmployer : "No employer found.");
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the top employer.", e);
        }

        try {
            // Most active student (by applications)
            String mostActiveStudent = userRepository.findMostActiveStudent();
            response.setMostActiveStudent(mostActiveStudent != null ? mostActiveStudent : "No active students found.");
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving the most active student.", e);
        }

        try {
            // Jobs by location
            Map<String, Integer> jobsByLocation = jobRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(
                            job -> job.getJobLocations().toString(),
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue) // Convert Long to Integer
                    ));
            response.setJobsByLocation(jobsByLocation.isEmpty() ? Collections.emptyMap() : jobsByLocation);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving jobs by location.", e);
        }

        return response;
    }
}
