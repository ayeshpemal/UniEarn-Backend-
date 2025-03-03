package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedEmployerResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.EmployerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployerServiceIMPL implements EmployerService {
    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private EmployerRepo employerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    private static final int PAGE_SIZE = 10; // Fixed page size

    @Override
    public String selectCandidate(long applicationId) {
        // Fetch the application by ID
        Application application = applicationRepo.findById(applicationId).orElse(null);

        if (application == null) {
            return "Application not found!";
        }

        // Update the application status to SELECTED (using the enum)
        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepo.save(application); // Save the updated application

        // Deactivate the job after selecting the candidate
        Job job = application.getJob();
        job.setActiveStatus(false); // Set the job as inactive
        jobRepo.save(job); // Save the updated job

        return "Candidate selected successfully for job: " + job.getJobTitle();
    }

    @Override
    public PaginatedEmployerResponseDTO getAllEmployers(int page) {
        Page<Employer> employerPage = employerRepo.findAll(PageRequest.of(page, PAGE_SIZE));
        List<UserResponseDTO> employers =employerPage.getContent()
                .stream()
                .map(employer -> modelMapper.map(employer, UserResponseDTO.class))
                .collect(Collectors.toList());

        long totalEmployers = employerRepo.count();

        return new PaginatedEmployerResponseDTO(employers, totalEmployers);
    }


}
