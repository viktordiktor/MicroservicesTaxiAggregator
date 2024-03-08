package com.nikonenko.rideservice.kafka.producer;

import com.nikonenko.rideservice.dto.RatingToDriverRequest;
import com.nikonenko.rideservice.utils.LogList;
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
public class UpdateDriverRatingRequestProducer {
    @Value("${spring.kafka.producer.driver-rating-topic.name}")
    private String ratingTopic;
    private final KafkaTemplate<String, RatingToDriverRequest> kafkaTemplate;

    public void sendRatingDriverRequest(RatingToDriverRequest request) {
        log.info(LogList.LOG_KAFKA_SEND_MESSAGE, request);
        Message<RatingToDriverRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, ratingTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
