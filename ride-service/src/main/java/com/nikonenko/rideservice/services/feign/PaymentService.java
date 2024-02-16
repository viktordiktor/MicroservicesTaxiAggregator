package com.nikonenko.rideservice.services.feign;

import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;

public interface PaymentService {
    CustomerChargeReturnResponse returnCharge(String chargeId);

    CustomerChargeResponse getChargeById(String chargeId);
}
