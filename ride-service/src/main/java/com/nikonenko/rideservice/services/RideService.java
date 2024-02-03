package com.nikonenko.rideservice.services;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RideResponse;

public interface RideService {
    CalculateDistanceResponse calculateDistance(CalculateDistanceRequest calculateDistanceRequest);
    RideResponse createRide(CreateRideRequest createRideRequest);
    RideResponse closeRide(Long rideId);
    PageResponse<RideResponse> getAvailableRides(int pageNumber, int pageSize, String sortField);
    PageResponse<RideResponse> getRidesByPassenger(Long passengerId, int pageNumber, int pageSize, String sortField);
    PageResponse<RideResponse> getRidesByDriver(Long driverId, int pageNumber, int pageSize, String sortField);
    RideResponse startRide(Long rideId, Long driverId);
}
