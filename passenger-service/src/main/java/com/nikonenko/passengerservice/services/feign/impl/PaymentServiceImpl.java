package com.nikonenko.passengerservice.services.feign.impl;

import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCreationRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCreationResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import com.nikonenko.passengerservice.feign.PaymentFeignClient;
import com.nikonenko.passengerservice.services.feign.PaymentService;
import com.nikonenko.passengerservice.utils.LogList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackPaymentService")
@Retry(name = "paymentRetry")
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentFeignClient paymentFeignClient;

    @Override
    public CustomerChargeResponse createCharge(CustomerChargeRequest customerChargeRequest) {
        return paymentFeignClient.customerCharge(customerChargeRequest);
    }

    @Override
    public CustomerExistsResponse checkCustomerExists(UUID passengerId) {
        return paymentFeignClient.isCustomerExists(passengerId);
    }

    @Override
    public CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest calculateRideRequest) {
        return paymentFeignClient.calculateRidePrice(calculateRideRequest);
    }

    public CustomerChargeResponse fallbackPaymentService(CustomerChargeRequest customerChargeRequest, Exception ex) {
        log.error(LogList.LOG_CREATE_CHARGE_FEIGN_ERROR, customerChargeRequest.getPassengerId(), ex.getMessage());
        return CustomerChargeResponse.builder()
                .id("")
                .passengerId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .success(false)
                .errorMessage(ex.getMessage())
                .build();
    }

    public CustomerExistsResponse fallbackPaymentService(UUID passengerId, Exception ex) {
        log.error(LogList.LOG_CHECK_CUSTOMER_EXISTS_FEIGN_ERROR, passengerId, ex.getMessage());
        return CustomerExistsResponse.builder()
                .isExists(false)
                .errorMessage(ex.getMessage())
                .build();
    }

    public CustomerCalculateRideResponse fallbackPaymentService(CustomerCalculateRideRequest calculateRideRequest,
                                                                Exception ex) {
        log.error(LogList.LOG_CALCULATE_RIDE_PRICE_FEIGN_ERROR, ex.getMessage());
        return CustomerCalculateRideResponse.builder()
                .rideLength(0.0)
                .rideDateTime(LocalDateTime.now())
                .price(BigDecimal.ZERO)
                .coupon("")
                .errorMessage(ex.getMessage())
                .build();
    }

    public CustomerCreationResponse fallbackPaymentService(CustomerCreationRequest customerCreationRequest,
                                                           Exception ex) {
        log.error(LogList.LOG_CREATE_CUSTOMER_FEIGN_ERROR, customerCreationRequest.getPassengerId(), ex.getMessage());
        return CustomerCreationResponse.builder()
                .id("")
                .phone("")
                .username("")
                .errorMessage(ex.getMessage())
                .build();
    }
}
