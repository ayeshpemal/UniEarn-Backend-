package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedRatingDTO;
import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.ReatingRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateRatingRequestDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Ratings;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.RatingType;
import com.finalproject.uni_earn.exception.AlreadyRatedException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.exception.UnauthurizedRatingException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.RatingRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.RatingService;
import com.finalproject.uni_earn.util.mappers.RatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class RatingServiceIMPL implements RatingService {

    // Define constants to avoid magic numbers
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

    @Autowired
    public RatingServiceIMPL(UserRepo userRepo, RatingRepo ratingRepo, ApplicationRepo applicationRepo,
                             JobRepo jobRepo, RatingMapper ratingMapper) {
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
        this.applicationRepo = applicationRepo;
        this.jobRepo = jobRepo;
        this.ratingMapper = ratingMapper;
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
                existingRating.getJob().getJobId() != requestDTO.getJobId()) {
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
                rating.getJob().getJobId(),
                rating.getCreatedAt()
        );
    }

    //Saying the rating
    RatingResponseBasicDTO saveRating(ReatingRequestDTO reatingRequestDTO) {
//        don't allow rating if rating is already made by that user, to that user, by his that job
        if (ratingRepo.existsByRaterRatedAndJob(reatingRequestDTO.getRaterId(),
                reatingRequestDTO.getRatedId(), reatingRequestDTO.getJobId())) {
            throw new AlreadyRatedException("You have already rated this user for this job");
        }

        Optional<String> roleofRater = userRepo.findRoleByUserId(reatingRequestDTO.getRaterId());
        Optional<String> roleofRated = userRepo.findRoleByUserId(reatingRequestDTO.getRatedId());
//validating Roles
        validateRoleIntegrity(roleofRater, roleofRated);

//Validating who is the employer and who is the student and RatingType
        RatingType ratingType = roleofRater.get().equals(STUDENT_ROLE) ?
                RatingType.STUDENT_TO_EMPLOYER : RatingType.EMPLOYER_TO_STUDENT;

        long employerId = roleofRater.get().equals(STUDENT_ROLE) ?
                reatingRequestDTO.getRatedId() : reatingRequestDTO.getRaterId();
        long studentId = roleofRater.get().equals(STUDENT_ROLE) ?
                reatingRequestDTO.getRaterId() : reatingRequestDTO.getRatedId();
        /*
         * validate the student employer job connection
         * 1)check whether employer have posted the job and is it finished
         * 2)check whether student applied that job and employer confirmed the application
         * there is two function for two tables
         * */
        if (!(jobRepo.existsByJobIdAndActiveStatusAndEmployer_UserId(reatingRequestDTO.getJobId(),
                false, employerId) &&
                applicationRepo.existsByStudent_UserIdAndJob_JobIdAndStatus(studentId,
                        reatingRequestDTO.getJobId(), ApplicationStatus.ACCEPTED))) {
            throw new UnauthurizedRatingException(
                    "Unauthorized Rating Request. Harms the integrity of the system");
        }
//        getting needed data
        Application application = applicationRepo.findByStudent_UserIdAndJob_JobId(
                studentId, reatingRequestDTO.getJobId());
        User rater = userRepo.getReferenceById(reatingRequestDTO.getRaterId());
        User rated = userRepo.getReferenceById(reatingRequestDTO.getRatedId());
        Job job = jobRepo.getReferenceById(reatingRequestDTO.getJobId());

        Ratings ratings = new Ratings(
                null,
                application,
                rater,
                rated,
                job,
                reatingRequestDTO.getScore(),
                reatingRequestDTO.getComment(),
                ratingType,
                null,
                null
        );

        Ratings storedRatings = ratingRepo.save(ratings);
        return createResponseDTO(storedRatings);
    }

    // updating the rating
    RatingResponseBasicDTO updateRating(UpdateRatingRequestDTO updateRatingRequestDTO) {
        /*here we first use the function to
        1) we don't allow peoples to change or delete the rating all the time
        * so we must calculate the rating creat time with time when request came
        * 2) the only same rater can change the rating and for only the rating that request job id says
        * and for only the same rater
        * */
        Ratings existingRating = validateAndGetRating(updateRatingRequestDTO, "updated");

        Optional<String> roleofRater = userRepo.findRoleByUserId(updateRatingRequestDTO.getRaterId());
        Optional<String> roleofRated = userRepo.findRoleByUserId(updateRatingRequestDTO.getRatedId());
//validating Roles
        validateRoleIntegrity(roleofRater, roleofRated);
//Validating who is the employer and who is the student and RatingType
        long employerId = roleofRater.get().equals(STUDENT_ROLE) ?
                updateRatingRequestDTO.getRatedId() : updateRatingRequestDTO.getRaterId();
        long studentId = roleofRater.get().equals(STUDENT_ROLE) ?
                updateRatingRequestDTO.getRaterId() : updateRatingRequestDTO.getRatedId();
        /*
         * validate the student employer job connection
         * 1)check whether employer have posted the job and is it finished
         * 2)check whether student applied that job and employer confirmed the application
         * there is two function for two tables
         * */
        if (!(jobRepo.existsByJobIdAndActiveStatusAndEmployer_UserId(
                updateRatingRequestDTO.getJobId(), false, employerId) &&
                applicationRepo.existsByStudent_UserIdAndJob_JobIdAndStatus(
                        studentId, updateRatingRequestDTO.getJobId(), ApplicationStatus.ACCEPTED))) {
            throw new UnauthurizedRatingException(
                    "Unauthorized Rating Request. Harms the integrity of the system");
        }
//update the comment and the score

        existingRating.setScore(updateRatingRequestDTO.getNewScore());
        existingRating.setComment(updateRatingRequestDTO.getNewComment());
//saving
        Ratings updatedRating = ratingRepo.save(existingRating);
        return createResponseDTO(updatedRating);
    }


    RatingResponseBasicDTO deleteRating(UpdateRatingRequestDTO updateRatingRequestDTO) {
        /*here we first use the function to
        1) we don't allow peoples to change or delete the rating all the time
        * so we must calculate the rating creat time with time when request came
        * 2) the only same rater can change the rating and for only the rating that request job id says
        * and for only the same rater
        * */
        Ratings existingRating = validateAndGetRating(updateRatingRequestDTO, "deleted");

        Optional<String> roleofRater = userRepo.findRoleByUserId(updateRatingRequestDTO.getRaterId());
        Optional<String> roleofRated = userRepo.findRoleByUserId(updateRatingRequestDTO.getRatedId());
//validating Roles
        validateRoleIntegrity(roleofRater, roleofRated);
//Validating who is the employer and who is the student and RatingType
        long employerId = roleofRater.get().equals(STUDENT_ROLE) ?
                updateRatingRequestDTO.getRatedId() : updateRatingRequestDTO.getRaterId();
        long studentId = roleofRater.get().equals(STUDENT_ROLE) ?
                updateRatingRequestDTO.getRaterId() : updateRatingRequestDTO.getRatedId();
        /*
         * validate the student employer job connection
         * 1)check whether employer have posted the job and is it finished
         * 2)check whether student applied that job and employer confirmed the application
         * there is two function for two tables
         * */
        if (!(jobRepo.existsByJobIdAndActiveStatusAndEmployer_UserId(
                updateRatingRequestDTO.getJobId(), false, employerId) &&
                applicationRepo.existsByStudent_UserIdAndJob_JobIdAndStatus(
                        studentId, updateRatingRequestDTO.getJobId(), ApplicationStatus.ACCEPTED))) {
            throw new UnauthurizedRatingException(
                    "Unauthorized rating deletion request - Invalid job or application status");
        }
//deleting

        RatingResponseBasicDTO responseDTO = createResponseDTO(existingRating);
        ratingRepo.delete(existingRating);

        return responseDTO;
    }

    PaginatedRatingDTO getAllReceivedRatings (long UserId,int page,int size)
    {
        Page<UserRatingDetailsResponseDTO> allReceivedRatings = ratingRepo.findAllRatingsReceivedByUser(UserId, PageRequest.of(page, size));
        return new PaginatedRatingDTO(allReceivedRatings.getContent(), ratingRepo.countAllByRatedUserId(UserId));
    }
    PaginatedRatingDTO getAllGivenRatings (long UserId,int page,int size)
    {
        Page<UserRatingDetailsResponseDTO> allReceivedRatings = ratingRepo.findAllRatingsGivenByUser(UserId, PageRequest.of(page, size));
        return new PaginatedRatingDTO(allReceivedRatings.getContent(), ratingRepo.countAllByRaterUserId(UserId));
    }
}