package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;

public interface PaymentCustomerService {
    CustomerCreationResponse createCustomer(CustomerCreationRequest customerRequest);

    CustomerChargeResponse customerCharge(CustomerChargeRequest request);

    CustomerCalculateRideResponse calculateRidePrice(CustomerCalculateRideRequest customerCalculateRideRequest);

    void returnCustomerCharge(String chargeId);
}
