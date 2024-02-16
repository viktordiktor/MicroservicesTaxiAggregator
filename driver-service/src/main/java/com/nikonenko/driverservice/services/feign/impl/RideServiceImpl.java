package com.nikonenko.driverservice.services.feign.impl;

import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.feign.RideFeignClient;
import com.nikonenko.driverservice.services.feign.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideFeignClient rideFeignClient;

    @Override
    public PageResponse<RideResponse> getRidesByDriverId(Long driverId) {
        return rideFeignClient.getRidesByDriverId(driverId);
    }
}
