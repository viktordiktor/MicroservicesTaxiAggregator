package com.nikonenko.driverservice.services.feign;

import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;

import java.util.UUID;

public interface RideService {
    PageResponse<RideResponse> getRidesByDriverId(UUID driverId, int pageNumber, int pageSize, String sortField);
}
