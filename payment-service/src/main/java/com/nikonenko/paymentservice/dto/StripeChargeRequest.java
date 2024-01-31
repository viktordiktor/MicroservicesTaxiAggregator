package com.nikonenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeChargeRequest {
    private String stripeToken;
    private String currency;
    private Double amount;
    private Map<String,Object> additionalInfo = new HashMap<>();
}
