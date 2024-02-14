package com.nikonenko.passengerservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.producer.passenger-review-topic.name}")
    private String passengerReviewProducerTopicName;

    @Value("${spring.kafka.producer.payment-customer-topic.name}")
    private String paymentCustomerProducerTopicName;

    @Bean
    public NewTopic passengerReviewProducerTopicName() {
        return TopicBuilder
                .name(passengerReviewProducerTopicName)
                .build();
    }

    @Bean
    public NewTopic paymentCustomerProducerTopicName() {
        return TopicBuilder
                .name(paymentCustomerProducerTopicName)
                .build();
    }
}