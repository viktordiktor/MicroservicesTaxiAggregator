package com.nikonenko.driverservice.kafka.consumer;

import com.nikonenko.driverservice.dto.RatingDriverRequest;
import com.nikonenko.driverservice.services.DriverService;
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
    public void handleChangeDriverRatingRequest(RatingDriverRequest request) {
        log.info("Receiver request for change rating Driver {}", request.getDriverId());
        driverService.createReview(request);
    }
}