package com.nikonenko.driverservice.services.feign;

import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;

public interface RideService {
    PageResponse<RideResponse> getRidesByDriverId(Long driverId, int pageNumber, int pageSize, String sortField);
}
