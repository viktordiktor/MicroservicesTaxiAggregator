package com.nikonenko.passengerservice.services.feign.impl;

import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.feign.RideFeignClient;
import com.nikonenko.passengerservice.services.feign.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideFeignClient rideFeignClient;

    @Override
    public CalculateDistanceResponse getRideDistance(CalculateDistanceRequest customerChargeRequest) {
        return rideFeignClient.calculateDistance(customerChargeRequest);
    }

    @Override
    public RideResponse createRide(CreateRideRequest createRideRequest) {
        return rideFeignClient.createRideRequest(createRideRequest);
    }
}
