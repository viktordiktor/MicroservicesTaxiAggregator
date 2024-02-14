package com.nikonenko.passengerservice.feign;

import com.nikonenko.passengerservice.config.feign.FeignConfig;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${feign.client.config.ride.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.config.ride.path}")
public interface RideFeignClient {
    @GetMapping("/distance")
    CalculateDistanceResponse calculateDistance(@SpringQueryMap CalculateDistanceRequest customerChargeRequest);

    @PostMapping
    RideResponse createRideRequest(@RequestBody CreateRideRequest createRideRequest);
}
