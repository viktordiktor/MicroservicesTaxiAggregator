package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeReturnResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerExistsResponse;

import java.time.LocalDateTime;

public interface PaymentCustomerService {
    CustomerCreationResponse createCustomer(CustomerCreationRequest customerRequest);

    CustomerChargeResponse createCustomerCharge(CustomerChargeRequest request);

    CustomerChargeResponse getCustomerCharge(String chargeId);

    CustomerCalculateRideResponse calculateRidePrice(Double rideLength, LocalDateTime rideDateTime, String coupon);

    CustomerChargeReturnResponse returnCustomerCharge(String chargeId);

    CustomerExistsResponse isCustomerExists(Long passengerId);
}
