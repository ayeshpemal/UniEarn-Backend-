package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.request.RecommendationRequestDTO;
import com.finalproject.uni_earn.service.JobEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobEventPublisherIMPL implements JobEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(JobEventPublisherIMPL.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchange = "job-events";

    @Autowired
    public JobEventPublisherIMPL(@Qualifier("customRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishJobCreatedEvent(RecommendationRequestDTO requestDTO) {
        System.out.println(requestDTO.getStartAt());
        rabbitTemplate.convertAndSend(exchange, "job.created", requestDTO);
        logger.info("Job created event sent for job: {}", requestDTO.getJobID());
    }

    @Override
    public void publishJobUpdatedEvent(RecommendationRequestDTO requestDTO) {
        rabbitTemplate.convertAndSend(exchange, "job.updated", requestDTO);
        logger.info("Job updated event sent for job: {}", requestDTO.getJobID());
    }

    @Override
    public void publishJobDeletedEvent(Long jobId) {
        rabbitTemplate.convertAndSend(exchange, "job.deleted", jobId);
        logger.info("Job deleted event sent for job: {}", jobId);
    }
}
