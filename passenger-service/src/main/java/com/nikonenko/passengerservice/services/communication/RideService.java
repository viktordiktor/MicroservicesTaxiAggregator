package com.nikonenko.passengerservice.services.communication;

import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;

import java.util.UUID;

public interface RideService {
    CalculateDistanceResponse getRideDistance(CalculateDistanceRequest customerChargeRequest);

    RideResponse createRide(CreateRideRequest createRideRequest);

    CloseRideResponse closeRide(String rideId);

    PageResponse<RideResponse> getRidesByPassengerId(UUID passengerId);
}
