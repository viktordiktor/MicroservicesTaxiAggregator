package com.nikonenko.rideservice.kafka.consumer;

import com.nikonenko.rideservice.dto.PassengerReviewRequest;
import com.nikonenko.rideservice.services.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverReviewRequestConsumer {
    private final RideService rideService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.driver-review-name}")
    public void handleChangeRideStatusRequest(PassengerReviewRequest request) {
        log.info("Receiver request for Driver request: Ride {}", request.getRideId());
        rideService.changePassengerRating(request);
    }
}
