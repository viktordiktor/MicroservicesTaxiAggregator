package com.nikonenko.rideservice.services;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;

import java.util.UUID;

public interface RideService {
    CalculateDistanceResponse calculateDistance(LatLng startGeo, LatLng engGeo);

    RideResponse createRide(CreateRideRequest createRideRequest);

    PageResponse<RideResponse> getOpenRides(int pageNumber, int pageSize, String sortField);

    RideResponse getRideById(String rideId);

    PageResponse<RideResponse> getRidesByPassenger(UUID passengerId, int pageNumber, int pageSize, String sortField);

    PageResponse<RideResponse> getRidesByDriver(UUID driverId, int pageNumber, int pageSize, String sortField);

    void changeDriverRating(ReviewRequest request);

    void changePassengerRating(ReviewRequest request);

    void changeRideStatus(ChangeRideStatusRequest request);

    CloseRideResponse closeRide(String rideId);
}
