package com.nikonenko.driverservice.services.communication;

import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RideService {
    Flux<RideResponse> getRidesByDriverId(UUID driverId, int pageNumber, int pageSize, String sortField);
}
