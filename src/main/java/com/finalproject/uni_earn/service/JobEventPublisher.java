package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.request.RecommendationRequestDTO;

public interface JobEventPublisher {
    void publishJobCreatedEvent(RecommendationRequestDTO requestDTO);
    void publishJobUpdatedEvent(RecommendationRequestDTO requestDTO);
    void publishJobDeletedEvent(Long jobId);
}
