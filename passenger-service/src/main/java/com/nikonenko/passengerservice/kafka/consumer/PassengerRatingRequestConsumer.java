package com.nikonenko.passengerservice.kafka.consumer;

import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.services.PassengerService;
import com.nikonenko.passengerservice.utils.LogList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerRatingRequestConsumer {
    private final PassengerService passengerService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.rating-topic-name}")
    public void handleChangeDriverRatingRequest(RatingToPassengerRequest request) {
        log.info(LogList.LOG_KAFKA_RECEIVE_CHANGE_RATING, request.getPassengerId());
        passengerService.createReview(request);
    }
}