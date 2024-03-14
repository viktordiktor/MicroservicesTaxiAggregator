package com.nikonenko.e2etests.clients;

import com.nikonenko.e2etests.config.feign.FeignConfig;
import com.nikonenko.e2etests.dto.CustomerCalculateRideRequest;
import com.nikonenko.e2etests.dto.CustomerCalculateRideResponse;
import com.nikonenko.e2etests.dto.CustomerChargeRequest;
import com.nikonenko.e2etests.dto.CustomerChargeResponse;
import com.nikonenko.e2etests.dto.CustomerExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.payment.name}",
        configuration = FeignConfig.class,
        url = "${feign.client.config.payment.url}")
public interface PaymentServiceClient {
    @GetMapping("/customers/ride-price")
    CustomerCalculateRideResponse calculateRidePrice(@SpringQueryMap CustomerCalculateRideRequest calculateRideRequest);

    @GetMapping("/customers/checkExists/{passengerId}")
    CustomerExistsResponse isCustomerExists(@PathVariable UUID passengerId);

    @PostMapping("/customers/charge")
    CustomerChargeResponse customerCharge(@RequestBody CustomerChargeRequest customerChargeRequest);
}
