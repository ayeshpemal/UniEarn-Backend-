package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedGroupApplicationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentApplicationDTO;
import com.finalproject.uni_earn.dto.Response.*;
import com.finalproject.uni_earn.dto.request.StudentSummaryRequestDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class ApplicationServiceIMPL implements ApplicationService {

    @Autowired
    private ApplicationRepo applicationRepository;

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private TeamRepo teamRepository;

    // Inject notification service
    @Autowired
    private UpdateNotificationService updateNotificationService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RatingRepo ratingRepo;

    @Override
    public String applyAsStudent(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user: Only students can apply for jobs"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job not found"));

        if (job.getRequiredWorkers() != 1) {
            throw new NotAcceptableStatusException("This job requires a team, please apply as a team.");
        }

        if (applicationRepository.existsByJob_JobIdAndStudent_UserId(jobId, studentId)) {
            throw new RuntimeException("You have already applied for this job.");
        }

        Application application = new Application();
        application.setJob(job);
        application.setStudent(student);
        application.setAppliedDate(new Date());
        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);

        updateNotificationService.createNotification(application.getApplicationId());
        return "Student application submitted successfully!";
    }

    @Override
    public String applyAsTeam(Long teamId, Long jobId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        team.getMembers().forEach(member -> {
            if (applicationRepository.isStudentInAppliedTeam(jobId,member.getUserId())) {
                throw new InvalidValueException("One or more team members have already applied for this job.");
            }
        });

        if (applicationRepository.existsByJob_JobIdAndTeam_Id(jobId, teamId)) {
            throw new AlreadyExistException("This team has already applied for this job.");
        }

        if (teamRepository.findMembersCountByTeamId(teamId) != job.getRequiredWorkers()) {
            throw new RuntimeException("Team size does not match required workers.");
        }

        Application application = new Application();
        application.setJob(job);
        application.setTeam(team);
        application.setAppliedDate(new Date());
        application.setStatus(ApplicationStatus.INACTIVE);

        applicationRepository.save(application);
        updateNotificationService.createNotification(application.getApplicationId());
        return "Team application submitted successfully!";
    }

    @Override
    public void updateStatus(Long applicationId, ApplicationStatus newStatus, User user) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        if (user.getRole() == Role.EMPLOYER) {
            if (newStatus == ApplicationStatus.ACCEPTED || newStatus == ApplicationStatus.REJECTED) {
                application.setStatus(newStatus);
            } else {
                throw new InvalidValueException("Employer can only accept or reject applications.");
            }
        } else if (user.getRole() == Role.STUDENT) {
            if (application.getStatus() == ApplicationStatus.ACCEPTED && newStatus == ApplicationStatus.CONFIRMED) {
                application.setStatus(newStatus);
            } else {
                throw new InvalidValueException("Student can only confirm an accepted application.");
            }
        } else if (user.getRole() == Role.ADMIN) {
            if (newStatus == ApplicationStatus.REJECTED) {
                application.setStatus(newStatus);
            } else {
                throw new InvalidValueException("Admin can only reject applications.");
            }
        }else {
            throw new InvalidValueException("Invalid user role.");
        }

        applicationRepository.save(application);
        updateNotificationService.createNotification(applicationId);
    }

    @Override
    public ApplicationDTO viewApplicationDetails(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setApplicationId(application.getApplicationId());
            applicationDTO.setJobId(application.getJob().getJobId());
            applicationDTO.setStudentId(application.getStudent().getUserId()); // Changed from getUserId() to getStudentId()
            applicationDTO.setStatus(application.getStatus().name());
            applicationDTO.setAppliedDate(application.getAppliedDate());

            return applicationDTO;
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    @Override
    public void deleteApplication(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            applicationRepository.deleteById(applicationId);
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    @Override
    public Map<String, Object> getStudentApplicationsSummary(StudentSummaryRequestDTO requestDTO) {
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + requestDTO.getStudentId()));


        List<Application> individualapplications = applicationRepository.findByStudentAndCreatedAtBetween(student, requestDTO.getStartDate(), requestDTO.getEndDate());
        List<Application> teamApplications = applicationRepository.findByStudentInTeamAndDateRange(requestDTO.getStudentId(),requestDTO.getStartDate(),requestDTO.getEndDate());
        
        List<Application> applications = new ArrayList<>();
        applications.addAll(individualapplications);
        applications.addAll(teamApplications);

        Map<ApplicationStatus, Long> statusCounts = applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));


        long totalApplications = applications.size();


        Map<String, Double> statusPercentages = new HashMap<>();
        statusCounts.forEach((status, count) -> {
            double percentage = (count * 100.0) / totalApplications;
            statusPercentages.put(status.name(), percentage);
        });


        Map<JobCategory, Long> categoryBreakdown = applications.stream()
                .collect(Collectors.groupingBy(
                        application -> application.getJob().getJobCategory(), // Group by JobCategory enum
                        Collectors.counting() // Count the number of applications in each category
                ));


        Map<String, Object> response = new HashMap<>();
        response.put("totalApplications", totalApplications);
        response.put("inactive", statusCounts.getOrDefault(ApplicationStatus.INACTIVE,0L));
        response.put("pending", statusCounts.getOrDefault(ApplicationStatus.PENDING, 0L));
        response.put("accepted", statusCounts.getOrDefault(ApplicationStatus.ACCEPTED, 0L));
        response.put("rejected", statusCounts.getOrDefault(ApplicationStatus.REJECTED, 0L));
        response.put("confirmed", statusCounts.getOrDefault(ApplicationStatus.CONFIRMED, 0L)); // Added CONFIRMED status
        response.put("statusPercentages", statusPercentages);
        response.put("categoryBreakdown", categoryBreakdown);

        return response;
    }

    @Override
    public List<GroupApplicationDTO> getGroupApplicationsByJobId(Long jobId) {
        // Check if the job requires a team
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with ID: " + jobId));
        if (job.getRequiredWorkers() == 1) {
            throw new InvalidValueException("This job does not require a team.");
        }
        // Fetch all pending group applications for the job
        List<Application> pendingGroupApplications = applicationRepository.findPendingGroupApplicationsByJobId(jobId);

        //List<Application> groupApplications = applicationRepository.

        // Transform the data into the required DTO structure
        return pendingGroupApplications.stream()
                .map(application -> {
                    Team team = application.getTeam();
                    return new GroupApplicationDTO(
                            application.getApplicationId(), // applicationId
                            team.getId(), // groupId
                            team.getTeamName(), // groupName
                            team.getMembers().stream()
                                    .map(member -> new GroupMemberDTO(
                                            member.getUserId(), // id
                                            member.getUserName(), // name
                                            member.getLocation(), // location
                                            member.getRating(), // rating
                                            member.getProfilePictureUrl(), // avatar
                                            ratingRepo.existsByRaterRatedAndApplication(
                                                    job.getEmployer().getUserId(),
                                                    member.getUserId(),
                                                    application.getApplicationId()) // Check if the student has rated the application
                                    ))
                                    .collect(Collectors.toList()) // members
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentApplicationDTO> getPendingStudentsByJobId(Long jobId) {
        List<Application> pendingApplications = applicationRepository.findPendingStudentApplicationsByJobId(jobId);

        return pendingApplications.stream()
                .map(application -> {
                    Student student = application.getStudent();
                    return new StudentApplicationDTO(
                            application.getApplicationId(),          // applicationId
                            student.getUserId(),          // id
                            student.getUserName(),       // name
                            student.getLocation(),        // location
                            student.getRating(),         // rating
                            student.getProfilePictureUrl() // profilePictureUrl
                    );
                })
                .collect(Collectors.toList());
    }

    public StudentApplicationResponseDTO hasStudentAppliedForJob(Long studentId, Long jobId) {
        if(!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found with ID: " + studentId);
        }
        if(!jobRepository.existsById(jobId)) {
            throw new NotFoundException("Job not found with ID: " + jobId);
        }
        StudentApplicationResponseDTO responseDTO = new StudentApplicationResponseDTO();

        Application studentApplication = applicationRepository.findByJob_JobIdAndStudent_UserId(jobId, studentId);
        Application groupApplication = applicationRepository.findStudentInAppliedTeam(jobId, studentId);

        if(studentApplication != null) {
            responseDTO.setHasApplied(true);
            responseDTO.setApplication(modelMapper.map(studentApplication, ApplicationDTO.class));
            responseDTO.setMemberStatus(null);
            responseDTO.setIsTeamLeader(null);
        }else if(groupApplication != null) {
            responseDTO.setHasApplied(true);
            responseDTO.setApplication(modelMapper.map(groupApplication, ApplicationDTO.class));
            Team team = groupApplication.getTeam();
            responseDTO.setMemberStatus(team.getMemberConfirmations().get(studentRepository.getStudentByUserId(studentId)));
            responseDTO.setIsTeamLeader(team.getLeader().getUserId() == studentId);
        }else{
            throw new NotFoundException("Application not found");
        }

        return responseDTO;
    }

    public PaginatedGroupApplicationDTO getPaginatedGroupApplicationsByJobId(Long jobId, int page, int pageSize) {
        // Check if the job requires a team
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with ID: " + jobId));
        if (job.getRequiredWorkers() == 1) {
            throw new InvalidValueException("This job does not require a team.");
        }

        // ✅ Fetch total application count for the job (without pagination)
        int totalApplicationCount = applicationRepository.countByJobId(jobId);

        // Fetch paginated applications for the job
        Page<Application> pagedApplications = applicationRepository.findByJob_JobId(jobId, PageRequest.of(page, pageSize));

        // ✅ Step 1: Check if there is a "CONFIRMED" application
        Optional<Application> confirmedApplication = pagedApplications.getContent().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.CONFIRMED)
                .findFirst();

        List<Application> filteredApplications;

        // ✅ Step 2: If "CONFIRMED" application exists, return only that one
        // ✅ Step 3: If no "CONFIRMED" application, return "PENDING" and "ACCEPTED" applications
        filteredApplications = confirmedApplication.map(List::of).orElseGet(() -> pagedApplications.getContent().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.ACCEPTED)
                .collect(Collectors.toList()));

        // ✅ Step 4: Convert to DTOs
        List<NewGroupApplicationDTO> dtos = filteredApplications.stream()
                .map(application -> {
                    Team team = application.getTeam();
                    return new NewGroupApplicationDTO(
                            application.getApplicationId(), // applicationId
                            team.getId(), // groupId
                            team.getTeamName(), // groupName
                            application.getStatus(), // applicationStatus
                            team.getMembers().stream()
                                    .map(member -> new GroupMemberDTO(
                                            member.getUserId(), // id
                                            member.getUserName(), // name
                                            member.getLocation(), // location
                                            member.getRating(), // rating
                                            member.getProfilePictureUrl(), // avatar
                                            ratingRepo.existsByRaterRatedAndApplication(
                                                    job.getEmployer().getUserId(),
                                                    member.getUserId(),
                                                    application.getApplicationId()) // Check if the student has rated the application
                                    ))
                                    .collect(Collectors.toList()) // members
                    );
                })
                .collect(Collectors.toList());

        // ✅ Step 5: Return result in PaginatedStudentApplicationDTO format
        return new PaginatedGroupApplicationDTO(dtos, totalApplicationCount, job.getJobStatus());
    }

    public PaginatedStudentApplicationDTO getPaginatedStudentApplicationsByJobId(Long jobId, int page, int pageSize) {
        // Check if the job requires a team
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with ID: " + jobId));
        if (job.getRequiredWorkers() != 1) {
            throw new InvalidValueException("This job requires a team.");
        }
        // Fetch total application count for the given job
        int totalApplications = applicationRepository.countByJobId(jobId);

        // Fetch all applications for the job with pagination
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Application> pagedApplications = applicationRepository.findByJob_JobId(jobId, pageable);

        // ✅ Step 1: Check if there is a "CONFIRMED" application
        Optional<Application> confirmedApplication = pagedApplications.getContent().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.CONFIRMED)
                .findFirst();

        List<Application> filteredApplications;

        // ✅ Step 2: If "CONFIRMED" application exists, return only that one
        // ✅ Step 3: If no "CONFIRMED" application, return "PENDING" and "ACCEPTED" applications
        filteredApplications = confirmedApplication.map(List::of).orElseGet(() -> pagedApplications.getContent().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.ACCEPTED)
                .collect(Collectors.toList()));

        List<NewStudentApplicationDTO> resultApplications = filteredApplications.stream()
                .map(application -> {
                    Student student = application.getStudent();
                    return new NewStudentApplicationDTO(
                            application.getApplicationId(),
                            student.getUserId(),
                            student.getUserName(),
                            student.getLocation(),
                            student.getRating(),
                            application.getStatus(),
                            student.getProfilePictureUrl(),
                            ratingRepo.existsByRaterRatedAndApplication(
                                    job.getEmployer().getUserId(),
                                    application.getStudent().getUserId(),
                                    application.getApplicationId()) // Check if the student has rated the application
                    );
                })
                .collect(Collectors.toList());

        // Construct the paginated response
        return new PaginatedStudentApplicationDTO(resultApplications, totalApplications, job.getJobStatus());
    }


}

