package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.FinishedJobDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.LocationDTO;
import com.finalproject.uni_earn.dto.NotificationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.DefaultRecommendationResponseDTO;
import com.finalproject.uni_earn.dto.Response.RecommendationResultDTO;
import com.finalproject.uni_earn.dto.request.AddJobRequestDTO;
import com.finalproject.uni_earn.dto.request.RecommendationRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateJobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.exception.InvalidParametersException;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.service.ApiClients.ApiClients;
import com.finalproject.uni_earn.service.JobEventPublisher;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.JobNotificationService;
import com.finalproject.uni_earn.specification.JobSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.finalproject.uni_earn.entity.enums.JobStatus.PENDING;

@Service
public class JobServiceIMPL implements JobService {

    Integer pageSize = 10;

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

    @Autowired
    private JobNotificationService notificationService;

    @Autowired
    private JobGroupMappingRepo jobGroupMappingRepo;

    @Autowired
    private RatingRepo ratingRepo;

    @Autowired
    private JobEventPublisher jobEventPublisher;

    @Autowired
    private UpdateNoRepo notificationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ApiClients apiClients;

// function for converting job to recommendation request
    private RecommendationRequestDTO createRecommendationRequest(Job job) {
        RecommendationRequestDTO requestDTO = new RecommendationRequestDTO();
        requestDTO.setJobID(job.getJobId());
        requestDTO.setTitle(job.getJobTitle());
        requestDTO.setJobDescription(job.getJobDescription());
        requestDTO.setCategory(job.getJobCategory().toString());
        requestDTO.setStatus(job.getJobStatus().toString());
        requestDTO.setStartAt(job.getStartDate().toString());
        System.out.println(requestDTO.getStartAt());
        requestDTO.setCompany(job.getEmployer().getCompanyName()); // Assuming Employer has companyName
        return requestDTO;
    }

    @Override
    public String addJob(AddJobRequestDTO addJobRequestDTO) {
        if (addJobRequestDTO.getJobLocations() == null || addJobRequestDTO.getJobLocations().isEmpty()) {
            throw new InvalidValueException("At least one location is required.");
        }

        if (addJobRequestDTO.getJobLocations().size() == 1) {
            Employer employer = employerRepo.getReferenceById(addJobRequestDTO.getEmployer());
            Job job = new Job();
            job.setJobTitle(addJobRequestDTO.getJobTitle());
            job.setJobCategory(addJobRequestDTO.getJobCategory());
            job.setJobDescription(addJobRequestDTO.getJobDescription());
            job.setJobPayment(addJobRequestDTO.getJobPayment());
            job.setRequiredWorkers(addJobRequestDTO.getRequiredWorkers());
            job.setRequiredGender(addJobRequestDTO.getRequiredGender());
            job.setStartDate(addJobRequestDTO.getJobLocations().get(0).getStartDate());
            job.setEndDate(addJobRequestDTO.getJobLocations().get(0).getEndDate());
            job.setStartTime(addJobRequestDTO.getStartTime());
            job.setEndTime(addJobRequestDTO.getEndTime());
            job.setEmployer(employer);
            job.setJobLocations(List.of(addJobRequestDTO.getJobLocations().get(0).getLocation()));
            job.setJobStatus(JobStatus.PENDING);

            Job savedJob = jobRepo.save(job);
//            Create a recommendation request for the saved job
            jobEventPublisher.publishJobCreatedEvent(createRecommendationRequest(savedJob));

            // Create follow notification after a new job is posted
            notificationService.createFollowNotification(employer, job); // Notify students about the new job
        }

        if (addJobRequestDTO.getJobLocations().size() > 1) {
            // Generate a unique ID for the group
            String groupJobId = UUID.randomUUID().toString();

            for (LocationDTO locationDTO : addJobRequestDTO.getJobLocations()) {
                Employer employer = employerRepo.getReferenceById(addJobRequestDTO.getEmployer());
                // Create a new Job for each location
                Job job = new Job();
                job.setJobTitle(addJobRequestDTO.getJobTitle());
                job.setJobCategory(addJobRequestDTO.getJobCategory());
                job.setJobDescription(addJobRequestDTO.getJobDescription());
                job.setJobPayment(addJobRequestDTO.getJobPayment());
                job.setRequiredWorkers(addJobRequestDTO.getRequiredWorkers());
                job.setRequiredGender(addJobRequestDTO.getRequiredGender());
                job.setStartDate(locationDTO.getStartDate());
                job.setEndDate(locationDTO.getEndDate());
                job.setStartTime(addJobRequestDTO.getStartTime());
                job.setEndTime(addJobRequestDTO.getEndTime());
                job.setEmployer(employer);
                job.setJobLocations(List.of(locationDTO.getLocation()));
                job.setJobStatus(JobStatus.PENDING);

                Job savedJob = jobRepo.save(job);
                // Create a recommendation request for the saved job
                jobEventPublisher.publishJobCreatedEvent(createRecommendationRequest(savedJob));
                // Create follow notification after a new job is posted
                notificationService.createFollowNotification(employer, job); // Notify students about the new job

                // Store the Job-Group mapping
                JobGroupMapping mapping = new JobGroupMapping();
                mapping.setGroupJobId(groupJobId);
                mapping.setJob(savedJob);
                jobGroupMappingRepo.save(mapping);
            }
        }

        return addJobRequestDTO.getJobTitle() + " is saved.";
    }

    @Override
    public String deleteJob(Long jobId) {
        if (jobRepo.existsByJobId(jobId)) {
            jobRepo.deleteById(jobId);
            jobEventPublisher.publishJobDeletedEvent(jobId);
            return "Job deleted";
        } else {
            throw new NotFoundException("No Job Found In That ID...!!");
        }
    }

    @Override
    public String updateJob(UpdateJobRequestDTO updateJobRequestDTO) {
        Job job = jobRepo.findById(updateJobRequestDTO.getJobId())
                .orElseThrow(() -> new NotFoundException("No Job Found In That ID...!!"));
        job.setJobTitle(updateJobRequestDTO.getJobTitle());
        job.setJobCategory(updateJobRequestDTO.getJobCategory());
        job.setJobDescription(updateJobRequestDTO.getJobDescription());
        job.setJobPayment(updateJobRequestDTO.getJobPayment());
        job.setRequiredWorkers(updateJobRequestDTO.getRequiredWorkers());
        job.setRequiredGender(updateJobRequestDTO.getRequiredGender());
        job.setStartDate(updateJobRequestDTO.getJobLocations().get(0).getStartDate());
        job.setEndDate(updateJobRequestDTO.getJobLocations().get(0).getEndDate());
        job.setStartTime(updateJobRequestDTO.getStartTime());
        job.setEndTime(updateJobRequestDTO.getEndTime());
        job.setEmployer(employerRepo.getReferenceById(updateJobRequestDTO.getEmployer()));
        job.setJobLocations(new ArrayList<>(List.of(updateJobRequestDTO.getJobLocations().get(0).getLocation())));
        job.setJobStatus(JobStatus.PENDING);
        Job updatedJob = jobRepo.save(job);
        jobEventPublisher.publishJobUpdatedEvent(createRecommendationRequest(updatedJob));

        return updateJobRequestDTO.getJobTitle() + " is updated...";
    }

    @Override
    public JobDTO viewJobDetails(long jobId) {
        Job job = jobRepo.getJobByJobId(jobId);
        if (job != null) {
            return modelMapper.map(job, JobDTO.class);
        } else {
            throw new NotFoundException("No Job Found In That ID...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO filterJobByCategory(JobCategory jobCategory, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobCategory(jobCategory, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobCategory(jobCategory)
            );
        } else {
            throw new NotFoundException("No Jobs Found...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContaining(location, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobLocationsContaining(location)
            );
        } else {
            throw new NotFoundException("No Jobs Found...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO searchJobByKeyword(String keyWord, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobDescriptionContaining(keyWord, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobDescriptionContaining(keyWord)
            );
        } else {
            throw new NotFoundException("No Jobs Founds...!!");
        }
    }
    @Override
    public PaginatedResponseJobDTO studentJobs(Long studentId, Integer page) {
        Student student = studentRepo.getStudentByUserId(studentId);
        if (student == null) {
            throw new NotFoundException("No Student Found In That ID...!!");
        }

        Location studentLocation = student.getLocation();
        List<JobCategory> preferences = student.getPreferences();

        // Try content-based filtering first
        try {
            // Get student's last two completed jobs
            List<FinishedJobDTO> lastTwoJobs = applicationRepo.findLastTwoFinishedJobsByStudent(studentId);

            // Only proceed if student has job history
            if (lastTwoJobs != null && !lastTwoJobs.isEmpty()) {
                // Create prompt with student preferences and job history
                String prompt = createStudentDetailsPrompt(lastTwoJobs, preferences);

                System.out.println("this happened");
                // Call Python service for recommendations
                DefaultRecommendationResponseDTO response = apiClients.recommend_jobs(prompt);

                System.out.println("this happened - 2");

                // Process response if successful
                if (response.isSuccess() && response.getData() != null) {
                    try {
                        // Parse recommendation data
                        @SuppressWarnings("unchecked")
                        List<RecommendationResultDTO> recommendations = (List<RecommendationResultDTO>) response.getData();

                        if (recommendations != null && !recommendations.isEmpty()) {
                            // Get job IDs from recommendations
                            List<Long> jobIds = recommendations.stream()
                                    .map(rec -> (long) rec.getJobId())
                                    .collect(Collectors.toList());

                            // Create similarity score map for sorting
                            Map<Long, Double> similarityScores = recommendations.stream()
                                    .collect(Collectors.toMap(
                                            rec -> (long) rec.getJobId(),
                                            RecommendationResultDTO::getSimilarityScore
                                    ));

                            // Find jobs and filter by location and status
                            List<Job> filteredJobs = jobRepo.findAllById(jobIds).stream()
                                    .filter(job -> job.getJobLocations().contains(studentLocation) &&
                                            job.getJobStatus() == PENDING)
                                    .sorted((j1, j2) -> Double.compare(
                                            similarityScores.getOrDefault(j2.getJobId(), 0.0),
                                            similarityScores.getOrDefault(j1.getJobId(), 0.0)))
                                    .collect(Collectors.toList());

                            if (!filteredJobs.isEmpty()) {
                                // Apply pagination
                                int start = page * pageSize;
                                int end = Math.min(start + pageSize, filteredJobs.size());

                                if (start < filteredJobs.size()) {
                                    List<Job> paginatedJobs = filteredJobs.subList(start, end);

                                    List<JobDTO> jobDTOs = paginatedJobs.stream()
                                            .map(job -> modelMapper.map(job, JobDTO.class))
                                            .collect(Collectors.toList());

                                    return new PaginatedResponseJobDTO(jobDTOs, filteredJobs.size());
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Log error and continue to fallback
                        System.err.println("Error processing recommendations: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // Log error and continue to fallback
            System.err.println("Error in content-based filtering: " + e.getMessage());
        }

        // Fallback to standard filtering method
        Page<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryInAndJobStatus(
                studentLocation, preferences, PENDING,
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt")));

        if (jobList.getSize() <= 0) {
            throw new NotFoundException("No Jobs Found...!!");
        }

        List<JobDTO> jobDTOs = jobList.getContent().stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());

        return new PaginatedResponseJobDTO(
                jobDTOs,
                jobRepo.countByJobStatusAndJobLocationsContainingAndJobCategoryIn(
                        JobStatus.PENDING, studentLocation, preferences)
        );
    }

    // Helper method to create prompt for Python service
    private String createStudentDetailsPrompt(List<FinishedJobDTO> lastTwoJobs, List<JobCategory> preferences) {
        StringBuilder prompt = new StringBuilder("my preferences are: ");

        // Add preferences
        for (int i = 0; i < preferences.size(); i++) {
            if (i > 0) {
                prompt.append(", ");
            }
            prompt.append(preferences.get(i).toString());
        }

        prompt.append("; i have completed jobs with: ");

        // Add completed jobs
        for (int i = 0; i < lastTwoJobs.size(); i++) {
            FinishedJobDTO job = lastTwoJobs.get(i);
            if (i > 0) {
                prompt.append(", ");
            }
            prompt.append("Title: ").append(job.getJobTitle())
                    .append(", Description: ").append(job.getJobDescription())
                    .append(", Employer: ").append(job.getEmployerName());
        }
        System.out.println(prompt);
        return prompt.toString();
    }



    @Override
    public PaginatedJobDetailsResponseDTO getJobsByUser(long userId, Integer page) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<JobDetailsResponseDTO> jobDetailsResponseDTOS = new ArrayList<>();
        long dataCount = 0;

        if (user instanceof Student student) {
            // Fetch all applied jobs count for students
            dataCount = applicationRepo.countByStudent(student) + applicationRepo.countApplicationsByStudentInTeam(student.getUserId());

            // Fetch paginated applied individual jobs for students
            Page<Application> individualApplications = applicationRepo.getAllByStudent(student, PageRequest.of(page, pageSize/2,Sort.by(Sort.Direction.DESC, "updatedAt")));

            // Get team applications
            Page<Application> teamApplications = applicationRepo.findApplicationsByStudentInTeam(student.getUserId(), PageRequest.of(page, pageSize/2,Sort.by(Sort.Direction.DESC, "updatedAt")));

            // Combine both lists, remove duplicates, and paginate
            Set<Application> allApplications = new HashSet<>(individualApplications.getContent());
            allApplications.addAll(teamApplications.getContent());

            List<Application> applicationList = new ArrayList<>(allApplications);

            jobDetailsResponseDTOS = applicationList.stream()
                    .map(application -> {
                        Job job = jobRepo.getJobByJobId(application.getJob().getJobId());
                        JobDetailsResponseDTO jobDetailsResponseDTO = modelMapper.map(job, JobDetailsResponseDTO.class);
                        jobDetailsResponseDTO.setJobLocation(job.getJobLocations().get(0).toString());
                        jobDetailsResponseDTO.setApplicationStatus(application.getStatus().toString());
                        jobDetailsResponseDTO.setRated(ratingRepo.existsByRaterRatedAndApplication(
                                userId,
                                job.getEmployer().getUserId(),
                                application.getApplicationId()));
                        jobDetailsResponseDTO.setApplicationId(application.getApplicationId());
                        return jobDetailsResponseDTO;
                    })
                    .collect(Collectors.toList());
        }

        if (user instanceof Employer employer) {
            // Fetch all posted jobs count for employers
            dataCount = jobRepo.countByEmployer(employer);

            // Fetch paginated posted jobs for employers
            Page<Job> jobs = jobRepo.findAllByEmployer(employer, PageRequest.of(page, pageSize,Sort.by(Sort.Direction.DESC, "updatedAt")));

            jobDetailsResponseDTOS = jobs.getContent().stream()
                    .map(job -> {
                        System.out.println(job);
                        JobDetailsResponseDTO jobDetailsResponseDTO = modelMapper.map(job, JobDetailsResponseDTO.class);
                        jobDetailsResponseDTO.setJobLocation(job.getJobLocations().get(0).toString());
                        jobDetailsResponseDTO.setApplicationStatus(null);
                        return jobDetailsResponseDTO;
                            }
                    )
                    .collect(Collectors.toList());
        }

        return new PaginatedJobDetailsResponseDTO(jobDetailsResponseDTOS, dataCount);
    }




    @Override
    public PaginatedResponseJobDTO searchJobs(Location location, List<JobCategory> categories, String keyword, Date startDateFrom, Date startDateTo, Integer page) {
        Specification<Job> spec = JobSpecification.filterJobs(location, categories, keyword, startDateFrom, startDateTo);
        if(spec==null)
            throw new InvalidParametersException("Invalid Parameters...!!");

        Page<Job> jobList = jobRepo.findAll(spec,PageRequest.of(page, pageSize,Sort.by(Sort.Direction.DESC, "updatedAt")));
        if(jobList.getSize()>0){
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.count(spec)
            );
        }
        else {
            throw new NotFoundException("No Jobs Founds...!!");
        }
    }

    public String setStatus(Long jobId, JobStatus status){
        if (!jobRepo.existsById(jobId)) {
            throw new NotFoundException("No Job Found with ID: " + jobId);
        }

        Job job = jobRepo.findById(jobId).
                orElseThrow(() -> new NotFoundException("No Job Found with ID: " + jobId));
        if(status == JobStatus.CANCEL){
            String message = String.format(
                    "Job %s has been cancelled by %s",
                    job.getJobTitle(),
                    job.getEmployer().getCompanyName()
            );
            List<Application> applications = applicationRepo.getByJob_JobId(job.getJobId());
            for (Application application : applications) {
                List<User> recipient = new ArrayList<>();

                if (application.getTeam() != null) {
                    recipient.addAll(application.getTeam().getMembers());
                }else{
                    recipient.add(application.getStudent());
                }
                for (User user : recipient) {
                    UpdateNotification notification = new UpdateNotification();
                    notification.setMessage(message);
                    notification.setRecipient(user);
                    notification.setApplication(application);
                    notification.setSentDate(new Date());
                    notification.setIsRead(false);
                    notificationRepo.save(notification);

                    NotificationDTO notificationDTO = new NotificationDTO(
                            notification.getId(),
                            message,
                            job.getJobId(),
                            notification.getIsRead(),
                            notification.getSentDate()
                    );
                    System.out.println("Member: " + user.getUserName());
                    // Send real-time notification to the specific recipient
                    messagingTemplate.convertAndSendToUser(
                            user.getUserName(),
                            "/topic/update-notifications",
                            notificationDTO
                    );
                }

                application.setStatus(ApplicationStatus.REJECTED);
                applicationRepo.save(application);
            }
        }
        job.setJobStatus(status);
        Job updatedJob = jobRepo.save(job);
        jobEventPublisher.publishJobUpdatedEvent(createRecommendationRequest(updatedJob));
        return "Set status: "+status;
    }
}
