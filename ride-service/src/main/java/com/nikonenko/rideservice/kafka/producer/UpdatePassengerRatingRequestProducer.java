package com.nikonenko.rideservice.kafka.producer;

import com.nikonenko.rideservice.dto.RatingDriverRequest;
import com.nikonenko.rideservice.dto.RatingPassengerRequest;
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
public class UpdatePassengerRatingRequestProducer {
    @Value("${spring.kafka.producer.passenger-rating-topic.name}")
    private String ratingTopic;
    private final KafkaTemplate<String, RatingDriverRequest> kafkaTemplate;

    public void sendRatingPassengerRequest(RatingPassengerRequest request) {
        log.info("Sending message {}", request);
        Message<RatingPassengerRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, ratingTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
