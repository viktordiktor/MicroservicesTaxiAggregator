package com.nikonenko.passengerservice.services.feign.impl;

import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCreationRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCreationResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import com.nikonenko.passengerservice.feign.PaymentFeignClient;
import com.nikonenko.passengerservice.services.feign.PaymentService;
import com.nikonenko.passengerservice.utils.ExceptionList;
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
    public CustomerChargeResponse createCharge(CustomerChargeRequest customerChargeRequest) {
        return paymentFeignClient.customerCharge(customerChargeRequest);
    }

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
    public CustomerExistsResponse checkCustomerExists(Long passengerId) {
        return paymentFeignClient.isCustomerExists(passengerId);
    }

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
    public CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest calculateRideRequest) {
        return paymentFeignClient.calculateRidePrice(calculateRideRequest);
    }

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
    public CustomerCreationResponse createCustomer(CustomerCreationRequest customerCreationRequest) {
        return paymentFeignClient.createCustomer(customerCreationRequest);
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
