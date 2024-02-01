package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.CustomerRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerRideResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerResponse;

public interface PaymentCustomerService {
    StripeCustomerResponse createCustomer(StripeCustomerRequest customerRequest);

    StripeCustomerChargeResponse customerCharge(StripeCustomerChargeRequest request);

    CustomerRideResponse calculateRidePrice(CustomerRideRequest customerRideRequest);
}
