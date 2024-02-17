package com.nikonenko.rideservice.services.feign.impl;

import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.feign.PaymentFeignClient;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.utils.ExceptionList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentFeignClient paymentFeignClient;

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
    public CustomerChargeReturnResponse returnCharge(String chargeId) {
        return paymentFeignClient.returnCharge(chargeId);
    }

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
    public CustomerChargeResponse getChargeById(String chargeId) {
        return paymentFeignClient.getChargeById(chargeId);
    }

    public PageResponse<CustomerChargeResponse> fallbackPaymentService(Exception ex) {
        log.info("Exception during request to Payment Service: {}", ex.getMessage());
        return PageResponse.<CustomerChargeResponse>builder()
                .objectList(Collections.emptyList())
                .totalElements(0)
                .totalPages(0)
                .errorMessage(ExceptionList.PAYMENT_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }
}
