package com.nikonenko.driverservice.kafka.producer;

import com.nikonenko.driverservice.dto.DriverReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverReviewRequestProducer {
    @Value("${spring.kafka.producer.driver-review-topic.name}")
    private String ratingTopic;
    private final KafkaTemplate<String, DriverReviewRequest> kafkaTemplate;

    public void sendRatingPassengerRequest(@Valid DriverReviewRequest request) {
        log.info("Sending message {}", request);
        Message<DriverReviewRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, ratingTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
