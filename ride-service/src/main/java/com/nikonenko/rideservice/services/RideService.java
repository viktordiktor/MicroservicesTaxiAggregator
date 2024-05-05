package com.nikonenko.rideservice.services;

import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RideService {
    Mono<CalculateDistanceResponse> calculateDistance(String startGeo, String engGeo);

    Mono<RideResponse> createRide(CreateRideRequest createRideRequest);

    Flux<RideResponse> getOpenRides(int pageNumber, int pageSize, String sortField);

    Mono<RideResponse> getRideById(String rideId);

    Flux<RideResponse> getRidesByPassenger(UUID passengerId, int pageNumber, int pageSize, String sortField);

    Flux<RideResponse> getRidesByDriver(UUID driverId, int pageNumber, int pageSize, String sortField);

    Mono<Void> changeDriverRating(ReviewRequest request);

    Mono<Void> changePassengerRating(ReviewRequest request);

    Mono<Void> changeRideStatus(ChangeRideStatusRequest request);

    Mono<CloseRideResponse> closeRide(String rideId);
}
