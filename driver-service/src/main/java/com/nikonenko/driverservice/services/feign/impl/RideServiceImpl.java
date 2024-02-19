package com.nikonenko.driverservice.services.feign.impl;

import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.feign.RideFeignClient;
import com.nikonenko.driverservice.services.feign.RideService;
import com.nikonenko.driverservice.utils.ExceptionList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
@Retry(name = "rideRetry")
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideFeignClient rideFeignClient;

    @Override
    public PageResponse<RideResponse> getRidesByDriverId(Long driverId) {
        return rideFeignClient.getRidesByDriverId(driverId);
    }

    public PageResponse<RideResponse> fallbackRideService(Long driverId, Exception ex) {
        log.info("Exception during getRidesByDriverId request to Ride Service: {}", ex.getMessage());
        return PageResponse.<RideResponse>builder()
                .objectList(Collections.emptyList())
                .totalElements(0)
                .totalPages(0)
                .errorMessage(ExceptionList.RIDE_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }
}
