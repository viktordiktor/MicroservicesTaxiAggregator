package com.nikonenko.rideservice.kafka.consumer;

import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.services.RideService;
import com.nikonenko.rideservice.utils.LogList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideStatusRequestConsumer {
    private final RideService rideService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.status-topic-name}")
    public void handleChangeRideStatusRequest(ChangeRideStatusRequest request) {
        log.info(LogList.LOG_KAFKA_RECEIVE_CHANGE_RIDE_STATUS, request.getRideId());
        rideService.changeRideStatus(request);
    }
}
