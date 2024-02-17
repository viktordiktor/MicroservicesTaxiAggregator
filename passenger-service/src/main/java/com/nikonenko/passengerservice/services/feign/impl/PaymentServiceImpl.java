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
import com.nikonenko.passengerservice.utils.ExceptionList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public CustomerExistsResponse checkCustomerExists(Long passengerId) {
        return paymentFeignClient.isCustomerExists(passengerId);
    }

    @Override
    public CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest calculateRideRequest) {
        return paymentFeignClient.calculateRidePrice(calculateRideRequest);
    }

    @Override
    public CustomerCreationResponse createCustomer(CustomerCreationRequest customerCreationRequest) {
        return paymentFeignClient.createCustomer(customerCreationRequest);
    }

    public CustomerChargeResponse fallbackPaymentService(CustomerChargeRequest customerChargeRequest, Exception ex) {
        log.info("Exception during createCharge request to Payment Service: {}", ex.getMessage());
        return CustomerChargeResponse.builder()
                .id("")
                .passengerId(0L)
                .amount(BigDecimal.ZERO)
                .success(false)
                .errorMessage(ExceptionList.PAYMENT_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public CustomerExistsResponse fallbackPaymentService(Long passengerId, Exception ex) {
        log.info("Exception during checkCustomerExists request to Payment Service: {}", ex.getMessage());
        return CustomerExistsResponse.builder()
                .isExists(false)
                .errorMessage(ExceptionList.PAYMENT_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public CustomerCalculateRideResponse fallbackPaymentService(CustomerCalculateRideRequest calculateRideRequest,
                                                                Exception ex) {
        log.info("Exception during calculateRidePrice request to Payment Service: {}", ex.getMessage());
        return CustomerCalculateRideResponse.builder()
                .rideLength(0.0)
                .rideDateTime(LocalDateTime.now())
                .price(BigDecimal.ZERO)
                .coupon("")
                .errorMessage(ExceptionList.PAYMENT_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }

    public CustomerCreationResponse fallbackPaymentService(CustomerCreationRequest customerCreationRequest,
                                                           Exception ex) {
        log.info("Exception during createCustomer request to Payment Service: {}", ex.getMessage());
        return CustomerCreationResponse.builder()
                .id("")
                .phone("")
                .username("")
                .errorMessage(ExceptionList.PAYMENT_SERVICE_NOT_AVAILABLE.getValue())
                .build();
    }
}
