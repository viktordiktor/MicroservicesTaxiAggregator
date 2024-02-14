package com.nikonenko.rideservice.kafka.consumer;

import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.services.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerReviewRequestConsumer {
    private final RideService rideService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.passenger-review-name}")
    public void handleChangeRideStatusRequest(ReviewRequest request) {
        log.info("Receiver request for Ride {}", request.getRideId());
        rideService.changeDriverRating(request);
    }
}
