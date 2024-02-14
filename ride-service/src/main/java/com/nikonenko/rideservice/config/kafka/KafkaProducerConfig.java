package com.nikonenko.rideservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.producer.driver-rating-topic.name}")
    private String driverRatingTopicName;
    @Value("${spring.kafka.producer.passenger-rating-topic.name}")
    private String passengerRatingTopicName;

    @Bean
    public NewTopic driverRatingProducerTopicName() {
        return TopicBuilder
                .name(driverRatingTopicName)
                .build();
    }

    @Bean
    public NewTopic passengerRatingProducerTopicName() {
        return TopicBuilder
                .name(passengerRatingTopicName)
                .build();
    }
}