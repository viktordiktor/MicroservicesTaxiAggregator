package com.nikonenko.passengerservice.feign;

import com.nikonenko.passengerservice.config.feign.FeignConfig;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${feign.client.config.payment.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.config.payment.path}")
public interface PaymentFeignClient {
    @PostMapping("/customers/charge")
    CustomerChargeResponse customerCharge(@RequestBody CustomerChargeRequest customerChargeRequest);

    @GetMapping("/customers/checkExists/{passengerId}")
    CustomerExistsResponse isCustomerExists(@PathVariable Long passengerId);

    @GetMapping("/customers/ride-price")
    CustomerCalculateRideResponse calculateRidePrice(
            @SpringQueryMap CustomerCalculateRideRequest calculateRideRequest);
}
