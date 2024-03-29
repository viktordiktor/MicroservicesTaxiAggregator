package com.nikonenko.driverservice.services.feign.impl;

import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.feign.RideFeignClient;
import com.nikonenko.driverservice.services.feign.RideService;
import com.nikonenko.driverservice.utils.LogList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
@Retry(name = "rideRetry")
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideFeignClient rideFeignClient;

    @Override
    public PageResponse<RideResponse> getRidesByDriverId(UUID driverId,
                                                         int pageNumber, int pageSize, String sortField) {
        return rideFeignClient.getRidesByDriverId(driverId, pageNumber, pageSize, sortField);
    }

    public PageResponse<RideResponse> fallbackRideService(UUID driverId, Exception ex) {
        log.error(LogList.LOG_GET_RIDES_BY_DRIVER_ID_FEIGN_ERROR, driverId, ex.getMessage());
        return PageResponse.<RideResponse>builder()
                .objectList(Collections.emptyList())
                .totalElements(0)
                .totalPages(0)
                .errorMessage(ex.getMessage())
                .build();
    }
}
