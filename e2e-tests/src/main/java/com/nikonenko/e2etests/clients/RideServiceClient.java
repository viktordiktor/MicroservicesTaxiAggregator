package com.nikonenko.e2etests.clients;

import com.nikonenko.e2etests.config.feign.FeignConfig;
import com.nikonenko.e2etests.dto.CalculateDistanceRequest;
import com.nikonenko.e2etests.dto.CalculateDistanceResponse;
import com.nikonenko.e2etests.dto.CloseRideResponse;
import com.nikonenko.e2etests.dto.CreateRideRequest;
import com.nikonenko.e2etests.dto.PageResponse;
import com.nikonenko.e2etests.dto.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${feign.client.config.ride.name}",
        configuration = FeignConfig.class,
        url = "${feign.client.config.ride.url}")
public interface RideServiceClient {
    @GetMapping("/by-passenger/{passengerId}")
    PageResponse<RideResponse> getRidesByPassengerId(@PathVariable Long passengerId);

    @GetMapping("/by-driver/{driverId}")
    PageResponse<RideResponse> getRidesByDriverId(@PathVariable Long driverId);

    @GetMapping("/distance")
    CalculateDistanceResponse calculateDistance(@SpringQueryMap CalculateDistanceRequest customerChargeRequest);

    @PostMapping
    RideResponse createRideRequest(@RequestBody CreateRideRequest createRideRequest);

    @DeleteMapping("/{rideId}")
    CloseRideResponse closeRide(@PathVariable String rideId);

    @GetMapping("/{rideId}")
    RideResponse getRide(@PathVariable String rideId);
}
