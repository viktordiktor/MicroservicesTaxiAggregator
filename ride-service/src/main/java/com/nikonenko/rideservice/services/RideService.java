package com.nikonenko.rideservice.services;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;

public interface RideService {
    CalculateDistanceResponse calculateDistance(CalculateDistanceRequest calculateDistanceRequest);

    RideResponse createRide(CreateRideRequest createRideRequest);

    PageResponse<RideResponse> getOpenRides(int pageNumber, int pageSize, String sortField);

    RideResponse getRideById(String rideId);

    PageResponse<RideResponse> getRidesByPassenger(Long passengerId, int pageNumber, int pageSize, String sortField);

    PageResponse<RideResponse> getRidesByDriver(Long driverId, int pageNumber, int pageSize, String sortField);

    void changeDriverRating(ReviewRequest request);

    void changePassengerRating(ReviewRequest request);

    void changeRideStatus(ChangeRideStatusRequest request);

    void closeRide(String rideId);
}
