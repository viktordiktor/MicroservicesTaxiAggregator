package com.nikonenko.e2etests.dto;

import com.nikonenko.e2etests.models.RidePaymentMethod;
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
