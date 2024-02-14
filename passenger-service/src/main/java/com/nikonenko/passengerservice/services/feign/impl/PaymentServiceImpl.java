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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}
