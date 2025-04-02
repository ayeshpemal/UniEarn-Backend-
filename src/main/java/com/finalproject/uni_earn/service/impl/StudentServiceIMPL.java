package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.dto.StudentDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.FollowRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceIMPL implements StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private JobService jobService;

    // Fixed page size
    private static final int PAGE_SIZE = 10;

    @Override
    public PaginatedUserResponseDTO getAllStudents(int page) {
        Page<Student> studentPage = studentRepo.findAll(PageRequest.of(page, PAGE_SIZE));
        List<UserResponseDTO> employers =studentPage.getContent()
                .stream()
                .map(student -> modelMapper.map(student, UserResponseDTO.class))
                .collect(Collectors.toList());

        long totalStudents = studentRepo.count();

        return new PaginatedUserResponseDTO(employers, totalStudents);
    }

    @Override
    public PaginatedStudentResponseDTO searchStudents(String query, int page) {
        Page<Student> studentPage = studentRepo.findByDisplayNameContainingIgnoreCase(query, PageRequest.of(page, PAGE_SIZE));
        List<StudentDTO> students = studentPage.getContent()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());

        long totalStudents = studentRepo.countByDisplayNameContainingIgnoreCase(query);

        return new PaginatedStudentResponseDTO(students, totalStudents);
    }

    @Override
    public PaginatedStudentResponseDTO searchStudentsWithFollowStatus(Long userId, String query, int page) {
        // Fetch the current student (the user making the request)
        Student currentStudent = studentRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + userId));

        // Perform the search operation (assuming searchStudents method returns paginated results)
        PaginatedStudentResponseDTO responseDTO = searchStudents(query, page);

        // Get the list of students from the response DTO
        List<StudentDTO> studentDTOList = responseDTO.getStudents();
        List<StudentDTO> activeStudentList = new ArrayList<>();

        // Iterate over the student list to check the follow status
        for (StudentDTO studentDTO : studentDTOList) {
            // Fetch the student entity from the database (you might want to use a custom query to optimize this)
            Student student = studentRepo.findById(studentDTO.getUserId())
                    .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentDTO.getUserId()));

            // Check if the current student follows this student
            boolean isFollow = currentStudent.getFollowing().contains(student);

            // Set the follow status in the DTO
            studentDTO.setFollow(isFollow);

            // Check if the student is not deleted
            if (!student.isDeleted()) {
                activeStudentList.add(studentDTO);
            }
        }

        // Set the updated list of active students back to the response DTO
        responseDTO.setStudents(activeStudentList);

        return responseDTO;
    }

    @Override
    public String applicationConfirm(Long applicationId, Long studentId) {
        // Fetch the application by ID
        Application application = applicationRepo.findById(applicationId).
                orElseThrow(() -> new NotFoundException("Application not found with ID: " + applicationId));

        // Check if the application is already confirmed
        if (application.getStatus().equals(ApplicationStatus.CONFIRMED)) {
            return "Application is already confirmed.";
        }

        // Check if the application is already rejected
        if (application.getStatus().equals(ApplicationStatus.REJECTED)) {
            return "Application is already rejected.";
        }

        // Update the application status to confirmed
        User emp = application.getJob().getEmployer();
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));
        if(application.getStatus().equals(ApplicationStatus.ACCEPTED)){
            applicationService.updateStatus(applicationId, ApplicationStatus.CONFIRMED,student);
        }else{
            throw new InvalidValueException("Application is not accepted yet!");
        }

        Job job = application.getJob();
        // Check if the job is already finished
        if (job.getJobStatus().equals(JobStatus.FINISH) || job.getJobStatus().equals(JobStatus.ON_GOING) || job.getJobStatus().equals(JobStatus.CANCEL)) {
            throw new InvalidValueException("Application is already finished or ongoing or canceled.");
        }
        // Fetch all applications for the job
        List<Application> allApplications = applicationRepo.getByJob_JobId(job.getJobId());
        // Reject all other applications for this job

        if(!allApplications.isEmpty()){
            allApplications
                    .stream().filter(app -> !app.getApplicationId().equals(application.getApplicationId()))
                    .forEach(app -> {
                        applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.REJECTED, emp);
                    });
        }

        // Deactivate the job after selecting the candidate
        //jobService.setStatus(job.getJobId(), JobStatus.ON_GOING);
        return "Job application confirmed successfully.";
    }


}
