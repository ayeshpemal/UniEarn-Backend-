package com.finalproject.uni_earn.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue jobCreatedQueue() {
        return new Queue("job-created", true);
    }

    @Bean
    public Queue jobUpdatedQueue() {
        return new Queue("job-updated", true);
    }

    @Bean
    public Queue jobDeletedQueue() {
        return new Queue("job-deleted", true);
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange("job-events");
    }

    @Bean
    public Binding jobCreatedBinding(Queue jobCreatedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobCreatedQueue).to(jobExchange).with("job.created");
    }

    @Bean
    public Binding jobUpdatedBinding(Queue jobUpdatedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobUpdatedQueue).to(jobExchange).with("job.updated");
    }

    @Bean
    public Binding jobDeletedBinding(Queue jobDeletedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobDeletedQueue).to(jobExchange).with("job.deleted");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "customRabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}