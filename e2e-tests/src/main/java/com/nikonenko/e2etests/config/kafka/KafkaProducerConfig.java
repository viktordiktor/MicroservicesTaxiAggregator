package com.nikonenko.e2etests.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.producer.payment-customer-topic.name}")
    private String paymentCustomerProducerTopicName;

    @Value("${spring.kafka.producer.status-producer-topic.name}")
    private String rideStatusProducerTopicName;

    @Value("${spring.kafka.producer.driver-review-topic.name}")
    private String driverReviewProducerTopicName;

    @Value("${spring.kafka.producer.passenger-review-topic.name}")
    private String passengerReviewProducerTopicName;

    @Bean
    public NewTopic rideStatusProducerTopicName() {
        return TopicBuilder
                .name(rideStatusProducerTopicName)
                .build();
    }

    @Bean
    public NewTopic paymentCustomerProducerTopicName() {
        return TopicBuilder
                .name(paymentCustomerProducerTopicName)
                .build();
    }

    @Bean
    public NewTopic driverReviewProducerTopicName() {
        return TopicBuilder
                .name(driverReviewProducerTopicName)
                .build();
    }

    @Bean
    public NewTopic passengerReviewProducerTopicName() {
        return TopicBuilder
                .name(passengerReviewProducerTopicName)
                .build();
    }
}