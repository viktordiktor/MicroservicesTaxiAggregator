package com.nikonenko.passengerservice.services.feign;

import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;

public interface PaymentService {
    CustomerChargeResponse createCharge(CustomerChargeRequest customerChargeRequest);

    CustomerExistsResponse checkCustomerExists(Long passengerId);

    CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest calculateRideRequest);
}
