package com.nikonenko.passengerservice.services;

import com.nikonenko.passengerservice.dto.CustomerDataRequest;
import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingFromPassengerRequest;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PassengerService {
    PageResponse<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField);

    PassengerResponse getPassengerById(UUID id);

    PassengerResponse createPassenger(OAuth2User principal);

    Mono<RideResponse> createRideByPassenger(UUID passengerId, RideByPassengerRequest rideByPassengerRequest);

    PassengerResponse editPassenger(UUID id, PassengerRequest passengerRequest);

    Mono<CloseRideResponse> closeRide(String rideId);

    Flux<RideResponse> getPassengerRides(UUID passengerId, int pageNumber, int pageSize, String sortField);

    void deletePassenger(UUID id);

    void sendReviewToDriver(String rideId, RatingFromPassengerRequest request);

    void createReview(RatingToPassengerRequest request);

    void createCustomerByPassenger(UUID passengerId, CustomerDataRequest dataRequest);
}
