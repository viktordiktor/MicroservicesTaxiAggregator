package com.nikonenko.driverservice.kafka.consumer;

import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.services.DriverService;
import com.nikonenko.driverservice.utils.LogList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverRatingRequestConsumer {
    private final DriverService driverService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.rating-topic-name}")
    public void handleChangeDriverRatingRequest(RatingToDriverRequest request) {
        log.info(LogList.LOG_KAFKA_RECEIVE_CHANGE_RATING, request.getDriverId());
        driverService.createReview(request);
    }
}