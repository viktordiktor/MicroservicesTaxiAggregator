package com.nikonenko.passengerservice.services.feign.impl;

import com.google.maps.model.LatLng;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeReturnResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.feign.RideFeignClient;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.models.feign.RideStatus;
import com.nikonenko.passengerservice.services.feign.RideService;
import com.nikonenko.passengerservice.utils.ExceptionList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
@Retry(name = "rideRetry")
@Slf4j
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

    @Override
    public CloseRideResponse closeRide(String rideId) {
        return rideFeignClient.closeRide(rideId);
    }

    @Override
    public PageResponse<RideResponse> getRidesByPassengerId(Long passengerId) {
        return rideFeignClient.getRidesByPassengerId(passengerId);
    }

    public CalculateDistanceResponse fallbackRideService(CalculateDistanceRequest customerChargeRequest, Exception ex) {
        log.info("Exception during getRideDistance request to Ride Service: {}", ex.getMessage());
        return CalculateDistanceResponse.builder()
                .distance(0.0)
                .startGeo(new LatLng())
                .endGeo(new LatLng())
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public RideResponse fallbackRideService(CreateRideRequest createRideRequest, Exception ex) {
        log.info("Exception during createRide request to Ride Service: {}", ex.getMessage());
        return RideResponse.builder()
                .chargeId("")
                .distance(0.0)
                .passengerId(0L)
                .driverId(0L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .paymentMethod(RidePaymentMethod.BY_CASH)
                .status(RideStatus.FINISHED)
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public CloseRideResponse fallbackRideService(String rideId, Exception ex) {
        log.info("Exception during closeRide request to Ride Service: {}", ex.getMessage());
        return CloseRideResponse.builder()
                .customerChargeReturnResponse(new CustomerChargeReturnResponse())
                .ridePaymentMethod(RidePaymentMethod.BY_CASH)
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public PageResponse<RideResponse> fallbackRideService(Long passengerId, Exception ex) {
        log.info("Exception during getRidesByPassengerId request to Ride Service: {}", ex.getMessage());
        return PageResponse.<RideResponse>builder()
                .objectList(Collections.emptyList())
                .totalElements(0)
                .totalPages(0)
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }
}
