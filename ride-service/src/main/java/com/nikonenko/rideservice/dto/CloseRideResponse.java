package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.models.RidePaymentMethod;
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
}
