package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedRatingDTO;
import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.ReatingRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateRatingRequestDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.RatingCategory;
import com.finalproject.uni_earn.entity.enums.RatingType;
import com.finalproject.uni_earn.exception.AlreadyRatedException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.exception.UnauthurizedRatingException;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.RatingService;
import com.finalproject.uni_earn.util.mappers.RatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingServiceIMPL implements RatingService {

    private static final long MAX_RATING_MODIFICATION_MINUTES = 720; // 12 hours
    private static final String STUDENT_ROLE = "STUDENT";
    private static final String EMPLOYER_ROLE = "EMPLOYER";
    private static final String ADMIN_ROLE = "ADMIN";

    // Repository dependencies
    private final UserRepo userRepo;
    private final RatingRepo ratingRepo;
    private final ApplicationRepo applicationRepo;
    private final JobRepo jobRepo;
    private final RatingMapper ratingMapper;
    private final StudentRepo studentRepo;
    private final EmployerRepo employerRepo;

    @Autowired
    public RatingServiceIMPL(UserRepo userRepo, RatingRepo ratingRepo, ApplicationRepo applicationRepo,
                             JobRepo jobRepo, RatingMapper ratingMapper,StudentRepo studentRepo,EmployerRepo employerRepo) {
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
        this.applicationRepo = applicationRepo;
        this.jobRepo = jobRepo;
        this.ratingMapper = ratingMapper;
        this.studentRepo = studentRepo;
        this.employerRepo = employerRepo;
    }

    /*
     * This function is for validate the Roles of the request .
     * Because every Role can't rate every role
     * */
    public void validateRoleIntegrity(Optional<String> roleofRater, Optional<String> roleofRated) {
        if (!(roleofRater.isPresent() && roleofRated.isPresent())) {
            throw new NotFoundException("One or both users not found in the system");
        }
        if (roleofRater.get().equals(STUDENT_ROLE) && roleofRated.get().equals(STUDENT_ROLE)) {
            throw new UnauthurizedRatingException("Students cannot rate other students");
        }
        if (roleofRater.get().equals(EMPLOYER_ROLE) && roleofRated.get().equals(EMPLOYER_ROLE)) {
            throw new UnauthurizedRatingException("Employers cannot rate other employers");
        }
        if (roleofRater.get().equals(ADMIN_ROLE) || roleofRated.get().equals(ADMIN_ROLE)) {
            throw new UnauthurizedRatingException("Administrators cannot participate in ratings");
        }
    }

    /*
     * This function is for validating the update or delete request authority to change the database
     * 1) we don't allow peoples to change or delete the rating all the time
     * so we must calculate the rating creat time with time when request came
     * 2) the only same rater can change the rating and for only the rating that request job id says
     * and for only the same rater
     * */

    private Ratings validateAndGetRating(UpdateRatingRequestDTO requestDTO, String operation) {
        if (!ratingRepo.existsByRatingId(requestDTO.getRatingId())) {
            throw new NotFoundException("Rating Not Found");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime creationTime = ratingRepo.findCreatedAtByRatingId(requestDTO.getRatingId())
                .orElseThrow(() -> new NotFoundException("Rating creation time not found"));
        Duration timeSinceCreation = Duration.between(creationTime, currentTime);

        if (timeSinceCreation.toMinutes() > MAX_RATING_MODIFICATION_MINUTES) {
            throw new UnauthurizedRatingException("Ratings can only be " + operation +
                    " within 12 hours of creation");
        }

        Ratings existingRating = ratingRepo.findById(requestDTO.getRatingId())
                .orElseThrow(() -> new NotFoundException("Rating Not Found"));

        if (existingRating.getRater().getUserId() != requestDTO.getRaterId() ||
                existingRating.getRated().getUserId() != requestDTO.getRatedId() ||
                existingRating.getApplication().getApplicationId() != requestDTO.getApplicationId()) {
            throw new UnauthurizedRatingException("Rating details do not match the original rating");
        }

        return existingRating;
    }

    /*
     * to create the responseDTO
     * */

    private RatingResponseBasicDTO createResponseDTO(Ratings rating) {
        return new RatingResponseBasicDTO(
                rating.getRatingId(),
                rating.getRated().getUserId(),
                rating.getRater().getUserId(),
                rating.getApplication().getJob().getJobId(),
                rating.getCreatedAt(),
                rating.getCategory() // Added category
        );
    }

    private void calculateAndSaveAverageRating(Long userId) {
        //calculating the average rating
        float averageRating = 0.0f;
        List<Ratings> ratingList = ratingRepo.findAllByRated_UserId(userId);
        if (ratingList.isEmpty()) {
            averageRating = 0.0f;
        }else{
            double sum = ratingList.stream().mapToDouble(Ratings::getScore).sum();
            averageRating = (float) (sum / ratingList.size());
            //updating the average rating
            if (averageRating > 5.0) {
                averageRating = 5.0f;
            } else if (averageRating < 0.0) {
                averageRating = 0.0f;
            }
        }

        //updating the average rating of the rated user
        User ratedUser = userRepo.getReferenceById(userId);
        if (ratedUser instanceof Student student){
            student.setRating(averageRating);
            studentRepo.save(student);
        }else if(ratedUser instanceof Employer employer){
            employer.setRating(averageRating);
            employerRepo.save(employer);
        }
    }

    //Saying the rating

    public RatingResponseBasicDTO saveRating(ReatingRequestDTO reatingRequestDTO) {
        // don't allow rating if rating is already made by that user, to that user, for this application
        if (ratingRepo.existsByRaterRatedAndApplication(
                reatingRequestDTO.getRaterId(),
                reatingRequestDTO.getRatedId(),
                reatingRequestDTO.getApplicationId())) {
            throw new AlreadyRatedException("You have already rated this user for this application");
        }

        Optional<String> roleofRater = userRepo.findRoleByUserId(reatingRequestDTO.getRaterId());
        Optional<String> roleofRated = userRepo.findRoleByUserId(reatingRequestDTO.getRatedId());
        // validating Roles
        validateRoleIntegrity(roleofRater, roleofRated);

        // Validating who is the employer and who is the student and RatingType
        RatingType ratingType = roleofRater.get().equals(STUDENT_ROLE) ?
                RatingType.STUDENT_TO_EMPLOYER : RatingType.EMPLOYER_TO_STUDENT;

        long employerId = roleofRater.get().equals(STUDENT_ROLE) ?
                reatingRequestDTO.getRatedId() : reatingRequestDTO.getRaterId();
        long studentId = roleofRater.get().equals(STUDENT_ROLE) ?
                reatingRequestDTO.getRaterId() : reatingRequestDTO.getRatedId();

        // Get the application
        Application application = applicationRepo.findById(reatingRequestDTO.getApplicationId())
                .orElseThrow(() -> new NotFoundException("Application not found"));

        Job job = application.getJob();

        /*
         * validate the student employer job connection
         * 1) check whether employer has posted the job and it is finished
         * 2) check whether student applied that job and application is accepted
         */
        if (!jobRepo.existsByJobIdAndJobStatusAndEmployer_UserId(job.getJobId(),
                JobStatus.FINISH, employerId)) {
            throw new UnauthurizedRatingException(
                    "Unauthorized Rating Request: Job not finished or doesn't belong to employer");
        }

        // Determine RatingCategory and validate student's eligibility
        RatingCategory ratingCategory = null;
        boolean isStudentEligible = false;

        // Case 1: Individual application
        if (application.getStudent() != null &&
                application.getStudent().getUserId() == studentId &&
                application.getStatus() == ApplicationStatus.CONFIRMED) {

            isStudentEligible = true;
            ratingCategory = RatingCategory.INDIVIDUAL_TO_INDIVIDUAL;
        }
        // Case 2: Team application
        else if (application.getTeam() != null &&
                application.getStatus() == ApplicationStatus.CONFIRMED) {

            // Check if student is part of the team
            Team team = application.getTeam();
            boolean isStudentInTeam = team.getMemberConfirmations().entrySet().stream()
                    .anyMatch(entry -> entry.getKey().getUserId() == studentId && entry.getValue());

            if (isStudentInTeam) {
                isStudentEligible = true;
                ratingCategory = RatingCategory.TEAM_TO_INDIVIDUAL;

            }
        } else {
            throw new UnauthurizedRatingException(
                    "Unauthorized Rating Request: Student not associated with this application");// Default
        }


        // Getting needed data
        User rater = userRepo.getReferenceById(reatingRequestDTO.getRaterId());
        User rated = userRepo.getReferenceById(reatingRequestDTO.getRatedId());


        Ratings ratings = new Ratings(
                null, // ratingId
                application,
                rater,
                rated,
                reatingRequestDTO.getScore(),
                reatingRequestDTO.getComment(),
                ratingType,
                ratingCategory
        );

        // Save the rating
        Ratings storedRatings = ratingRepo.save(ratings);

        // Calculate and save average rating
        calculateAndSaveAverageRating(reatingRequestDTO.getRatedId());

        return createResponseDTO(storedRatings);
    }

    // updating the rating
    public RatingResponseBasicDTO updateRating(UpdateRatingRequestDTO updateRatingRequestDTO) {
        Ratings existingRating = validateAndGetRating(updateRatingRequestDTO, "updated");

        // Validate roles
        Optional<String> roleofRater = userRepo.findRoleByUserId(updateRatingRequestDTO.getRaterId());
        Optional<String> roleofRated = userRepo.findRoleByUserId(updateRatingRequestDTO.getRatedId());
        validateRoleIntegrity(roleofRater, roleofRated);

        // Get job from persisted rating (not DTO)
        Job job = existingRating.getApplication().getJob();
        long employerId = roleofRater.get().equals(STUDENT_ROLE)
                ? updateRatingRequestDTO.getRatedId()
                : updateRatingRequestDTO.getRaterId();

        // Validate job
        if (job.getJobStatus() != JobStatus.FINISH ||
                job.getEmployer().getUserId() != employerId) {
            throw new UnauthurizedRatingException("Invalid job state");
        }

        // Validate application
        Application application = existingRating.getApplication();
        if (application.getStatus() != ApplicationStatus.CONFIRMED) {
            throw new UnauthurizedRatingException("Application not confirmed");
        }

        // Validate team membership if applicable
        if (application.getTeam() != null) {
            Team team = application.getTeam();
            boolean isStudentInTeam = team.getMemberConfirmations().entrySet().stream()
                    .anyMatch(e -> e.getKey().getUserId() == existingRating.getRater().getUserId()
                            && e.getValue());
            if (!isStudentInTeam) {
                throw new UnauthurizedRatingException("Student not in application's team");
            }
        }

        // Update fields
        existingRating.setScore(updateRatingRequestDTO.getNewScore());
        existingRating.setComment(updateRatingRequestDTO.getNewComment());

        return createResponseDTO(ratingRepo.save(existingRating));
    }


    public RatingResponseBasicDTO deleteRating(UpdateRatingRequestDTO updateRatingRequestDTO) {
        Ratings existingRating = validateAndGetRating(updateRatingRequestDTO, "deleted");

        // Get job from existing rating's application (secure source)
        Job job = existingRating.getApplication().getJob();

        // Validate job status
        if (job.getJobStatus() != JobStatus.FINISH) {
            throw new UnauthurizedRatingException("Job not finished");
        }

        // Validate application status
        if (existingRating.getApplication().getStatus() != ApplicationStatus.CONFIRMED) {
            throw new UnauthurizedRatingException("Application not confirmed");
        }

        // Team validation (if applicable)
        if (existingRating.getApplication().getTeam() != null) {
            Team team = existingRating.getApplication().getTeam();
            boolean isRaterInTeam = team.getMemberConfirmations().entrySet().stream()
                    .anyMatch(e -> e.getKey().getUserId() == existingRating.getRater().getUserId()
                            && e.getValue());
            if (!isRaterInTeam) {
                throw new UnauthurizedRatingException("Rater not in application's team");
            }
        }

        ratingRepo.delete(existingRating);
        return createResponseDTO(existingRating);
    }

    public PaginatedRatingDTO getAllReceivedRatings (long UserId,int page,int size)
    {
        Page<UserRatingDetailsResponseDTO> allReceivedRatings = ratingRepo.findAllRatingsReceivedByUser(UserId, PageRequest.of(page, size));
        return new PaginatedRatingDTO(allReceivedRatings.getContent(), ratingRepo.countAllByRatedUserId(UserId));
    }
    public PaginatedRatingDTO getAllGivenRatings (long UserId,int page,int size)
    {
        Page<UserRatingDetailsResponseDTO> allReceivedRatings = ratingRepo.findAllRatingsGivenByUser(UserId, PageRequest.of(page, size));
        return new PaginatedRatingDTO(allReceivedRatings.getContent(), ratingRepo.countAllByRaterUserId(UserId));
    }


}