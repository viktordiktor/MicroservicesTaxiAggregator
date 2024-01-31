package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerResponse;

public interface StripeCustomerService {
    StripeCustomerResponse createCustomer(StripeCustomerRequest customerRequest);

    StripeCustomerChargeResponse customerCharge(StripeCustomerChargeRequest request);
}
