package com.nikonenko.paymentservice.dto.customers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeCustomerChargeResponse {
    private String id;
    private Long passengerId;
    private String currency;
    private Double amount;
    private boolean success;
}
