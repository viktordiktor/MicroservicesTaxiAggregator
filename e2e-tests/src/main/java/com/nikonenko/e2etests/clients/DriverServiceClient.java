package com.nikonenko.e2etests.clients;

import com.nikonenko.e2etests.config.feign.FeignConfig;
import com.nikonenko.e2etests.dto.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.driver.name}",
        configuration = FeignConfig.class,
        url = "${feign.client.config.driver.url}")
public interface DriverServiceClient {
    @GetMapping("/{driverId}")
    DriverResponse getDriver(@PathVariable UUID driverId);
}