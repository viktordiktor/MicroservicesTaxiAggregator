package com.nikonenko.passengerservice.kafka.producer;

import com.nikonenko.passengerservice.dto.PassengerReviewRequest;
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
public class PassengerReviewRequestProducer {
    @Value("${spring.kafka.producer.passenger-review-topic.name}")
    private String ratingTopic;
    private final KafkaTemplate<String, PassengerReviewRequest> kafkaTemplate;

    public void sendRatingDriverRequest(@Valid PassengerReviewRequest request) {
        log.info("Sending message {}", request);
        Message<PassengerReviewRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, ratingTopic)
                .build();
        kafkaTemplate.send(message);
    }
}