package com.nikonenko.passengerservice.services.feign;

import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;

import java.util.UUID;

public interface PaymentService {
    CustomerChargeResponse createCharge(CustomerChargeRequest customerChargeRequest);

    CustomerExistsResponse checkCustomerExists(UUID passengerId);

    CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest calculateRideRequest);
}
