package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.dto.Paginated.PaginatedEmployerResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.dto.StudentDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.EmployerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
    @Autowired
    private FollowRepo followRepo;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public String selectCandidate(long applicationId) {
        // Fetch the application by ID
        Application application = applicationRepo.findById(applicationId).orElse(null);

        if (application == null) {
            return "Application not found!";
        }
        User emp = application.getJob().getEmployer();
        // Update the application status to SELECTED (using the enum)
        applicationService.updateStatus(applicationId, ApplicationStatus.ACCEPTED,emp);

        // Deactivate the job after selecting the candidate
        Job job = application.getJob();
        // Reject all other applications for this job
        if (job.getRequiredWorkers() == 1){
            applicationService.getPendingStudentsByJobId(job.getJobId())
                    .stream().filter(app -> !app.getApplicationId().equals(application.getApplicationId()))
                    .forEach(app -> {
                        applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.REJECTED, emp);
                    });
        }else if (job.getRequiredWorkers() > 1){
            applicationService.getGroupApplicationsByJobId(job.getJobId())
                    .stream().filter(app -> !app.getApplicationId().equals(application.getApplicationId()))
                    .forEach(app -> {
                        applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.REJECTED, emp);
                    });
        }else{
            throw new NotFoundException("Application not found!");
        }

        job.setActiveStatus(false); // Set the job as inactive
        jobRepo.save(job); // Save the updated job

        return "Candidate selected successfully for job: " + job.getJobTitle();
    }

    @Override
    public PaginatedUserResponseDTO getAllEmployers(int page) {
        Page<Employer> employerPage = employerRepo.findAll(PageRequest.of(page, PAGE_SIZE));
        List<UserResponseDTO> employers =employerPage.getContent()
                .stream()
                .map(employer -> modelMapper.map(employer, UserResponseDTO.class))
                .collect(Collectors.toList());

        long totalEmployers = employerRepo.count();

        return new PaginatedUserResponseDTO(employers, totalEmployers);
    }

    @Override
    public PaginatedEmployerResponseDTO searchEmployers(String query, int page) {
        Page<Employer> employerPage = employerRepo.findByCompanyNameContainingIgnoreCase(query, PageRequest.of(page, PAGE_SIZE));
        List<EmployerDto> employers = employerPage.getContent()
                .stream()
                .map(employer -> modelMapper.map(employer, EmployerDto.class))
                .collect(Collectors.toList());

        long totalEmployers = employerRepo.countByCompanyNameContainingIgnoreCase(query);

        return new PaginatedEmployerResponseDTO(employers, totalEmployers);
    }

    @Override
    public PaginatedEmployerResponseDTO searchEmployersWithFollowStatus(Long userId, String query, int page) {
        // Fetch the current student (the user making the request)
        Student currentStudent = studentRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + userId));

        // Perform the search operation
        PaginatedEmployerResponseDTO responseDTO = searchEmployers(query, page);

        // Get the list of employers from the response DTO
        List<EmployerDto> employerList = responseDTO.getEmployers();

        // Iterate over the employer list to check the follow status
        for (EmployerDto employerDto : employerList) {
            Employer employer = employerRepo.findById(employerDto.getUserId())
                    .orElseThrow(() -> new NotFoundException("Employer not found with ID: " + employerDto.getUserId()));
            // Check if the current student follows this employer
            boolean isFollow = followRepo.existsByStudentAndEmployer(currentStudent, employer);

            // Set the follow status in the DTO
            employerDto.setFollow(isFollow);
        }

        // Set the updated list of employers back to the response DTO
        responseDTO.setEmployers(employerList);

        return responseDTO;
    }


}
