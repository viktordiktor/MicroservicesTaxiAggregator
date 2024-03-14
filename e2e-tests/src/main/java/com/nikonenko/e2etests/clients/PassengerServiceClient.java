package com.nikonenko.e2etests.clients;

import com.nikonenko.e2etests.config.feign.FeignConfig;
import com.nikonenko.e2etests.dto.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.passenger.name}",
        configuration = FeignConfig.class,
        url = "${feign.client.config.passenger.url}")
public interface PassengerServiceClient {
    @GetMapping("/{passengerId}")
    PassengerResponse getPassenger(@PathVariable UUID passengerId);
}
