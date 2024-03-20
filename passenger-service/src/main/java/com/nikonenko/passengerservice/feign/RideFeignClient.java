package com.nikonenko.passengerservice.feign;

import com.nikonenko.passengerservice.config.feign.FeignConfig;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.ride.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.config.ride.path}")
public interface RideFeignClient {
    @GetMapping("/distance")
    CalculateDistanceResponse calculateDistance(@SpringQueryMap CalculateDistanceRequest customerChargeRequest);

    @PostMapping
    RideResponse createRideRequest(@RequestBody CreateRideRequest createRideRequest);

    @DeleteMapping("/{rideId}")
    CloseRideResponse closeRide(@PathVariable String rideId);

    @GetMapping("/by-passenger/{passengerId}")
    PageResponse<RideResponse> getRidesByPassengerId(@PathVariable UUID passengerId);
}
