package com.nikonenko.driverservice.feign;

import com.nikonenko.driverservice.config.feign.FeignConfig;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.ride.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.config.ride.path}")
public interface RideFeignClient {
    @GetMapping("/by-driver/{driverId}")
    PageResponse<RideResponse> getRidesByDriverId(@PathVariable UUID driverId,
                                                  @RequestParam(defaultValue = "0") int pageNumber,
                                                  @RequestParam(defaultValue = "5") int pageSize,
                                                  @RequestParam(defaultValue = "id") String sortField);
}

