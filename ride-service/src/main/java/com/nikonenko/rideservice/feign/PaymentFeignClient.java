package com.nikonenko.rideservice.feign;

import com.nikonenko.rideservice.config.feign.FeignConfig;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${feign.client.config.payment.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.config.payment.path}")
public interface PaymentFeignClient {
    @PostMapping("/customers/charge/{chargeId}/return")
    CustomerChargeReturnResponse returnCharge(@PathVariable String chargeId);

    @GetMapping("/customers/charge/{chargeId}")
    CustomerChargeResponse getChargeById(@PathVariable String chargeId);
}
