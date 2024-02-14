package com.nikonenko.passengerservice.services;

import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingFromPassengerRequest;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;

public interface PassengerService {
    PageResponse<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField);

    PassengerResponse getPassengerById(Long id);

    PassengerResponse createPassenger(PassengerRequest passengerRequest);

    RideResponse createRideByPassenger(Long passengerId, RideByPassengerRequest rideByPassengerRequest);

    PassengerResponse editPassenger(Long id, PassengerRequest passengerRequest);

    void deletePassenger(Long id);

    void sendReviewToDriver(String rideId, RatingFromPassengerRequest request);

    void createReview(RatingToPassengerRequest request);
}
