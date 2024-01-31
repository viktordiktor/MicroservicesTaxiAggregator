package com.nikonenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeChargeResponse {
    private Double amount;
    private Boolean success;
    private String message;
    private String chargeId;
}
