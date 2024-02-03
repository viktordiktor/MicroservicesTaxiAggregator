package com.nikonenko.rideservice.services;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.CreateRideResponse;

public interface RideService {
    CalculateDistanceResponse calculateDistance(CalculateDistanceRequest calculateDistanceRequest);
    CreateRideResponse createRide(CreateRideRequest createRideRequest);
}
