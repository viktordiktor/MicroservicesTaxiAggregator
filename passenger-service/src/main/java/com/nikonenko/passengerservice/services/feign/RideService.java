package com.nikonenko.passengerservice.services.feign;

import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;

public interface RideService {
    CalculateDistanceResponse getRideDistance(CalculateDistanceRequest customerChargeRequest);

    RideResponse createRide(CreateRideRequest createRideRequest);

    CloseRideResponse closeRide(String rideId);
}
