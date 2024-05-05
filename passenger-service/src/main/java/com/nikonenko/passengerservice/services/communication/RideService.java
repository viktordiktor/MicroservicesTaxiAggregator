package com.nikonenko.passengerservice.services.communication;

import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RideService {
    CalculateDistanceResponse getRideDistance(CalculateDistanceRequest customerChargeRequest);

    Mono<RideResponse> createRide(CreateRideRequest createRideRequest);

    Mono<CloseRideResponse> closeRide(String rideId);

    Flux<RideResponse> getRidesByPassengerId(UUID passengerId, int pageNumber, int pageSize, String sortField);
}
