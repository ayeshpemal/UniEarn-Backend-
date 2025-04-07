package com.finalproject.uni_earn.service.ApiClients;

import com.finalproject.uni_earn.dto.Response.DefaultRecommendationResponseDTO;
import com.finalproject.uni_earn.dto.request.RecommendationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(url = "http://localhost:8000", name = "recommendation-service")
public interface ApiClients {
    @PostMapping(value = "/recommendation/save-jobs")
      DefaultRecommendationResponseDTO save_jobs(RecommendationRequestDTO recommendationRequestDTO);

    @PostMapping(value = "/recommendation/recommend-jobs" )
    DefaultRecommendationResponseDTO recommend_jobs(String StudentDetails);

    @DeleteMapping(value = "/recommendation/delete-job/{job_id}")
    DefaultRecommendationResponseDTO delete_job(@PathVariable Integer job_id);

    @PutMapping(value = "/recommendation//update-job")
    DefaultRecommendationResponseDTO update_job(RecommendationRequestDTO recommendationRequestDTO);

}
