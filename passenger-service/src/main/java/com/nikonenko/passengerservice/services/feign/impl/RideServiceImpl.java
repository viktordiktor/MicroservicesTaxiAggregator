package com.nikonenko.passengerservice.services.feign.impl;

import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.feign.RideFeignClient;
import com.nikonenko.passengerservice.services.feign.RideService;
import com.nikonenko.passengerservice.utils.ExceptionList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideFeignClient rideFeignClient;

    @Override
    @CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
    public CalculateDistanceResponse getRideDistance(CalculateDistanceRequest customerChargeRequest) {
        return rideFeignClient.calculateDistance(customerChargeRequest);
    }

    @Override
    @CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
    public RideResponse createRide(CreateRideRequest createRideRequest) {
        return rideFeignClient.createRideRequest(createRideRequest);
    }

    @Override
    @CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
    public CloseRideResponse closeRide(String rideId) {
        return rideFeignClient.closeRide(rideId);
    }

    @Override
    @CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
    public PageResponse<RideResponse> getRidesByPassengerId(Long passengerId) {
        return rideFeignClient.getRidesByPassengerId(passengerId);
    }

    public PageResponse<RideResponse> fallbackRideService(Exception ex) {
        log.info("Exception during request to Ride Service: {}", ex.getMessage());
        return PageResponse.<RideResponse>builder()
                .objectList(Collections.emptyList())
                .totalElements(0)
                .totalPages(0)
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }
}
