package com.nikonenko.passengerservice.dto.feign.ride;

import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeReturnResponse;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseRideResponse {
    private RidePaymentMethod ridePaymentMethod;
    private CustomerChargeReturnResponse customerChargeReturnResponse;
    private String errorMessage;
}
