package com.nikonenko.rideservice.services.feign.impl;

import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.feign.PaymentFeignClient;
import com.nikonenko.rideservice.services.feign.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentFeignClient paymentFeignClient;

    @Override
    public CustomerChargeReturnResponse returnCharge(String chargeId) {
        return paymentFeignClient.returnCharge(chargeId);
    }

    @Override
    public CustomerChargeResponse getChargeById(String chargeId) {
        return paymentFeignClient.getChargeById(chargeId);
    }
}
