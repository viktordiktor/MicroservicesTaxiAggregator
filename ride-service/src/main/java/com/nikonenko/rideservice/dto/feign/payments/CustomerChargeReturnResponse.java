package com.nikonenko.rideservice.dto.feign.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerChargeReturnResponse {
    private String id;
    private BigDecimal amount;
    private String currency;
    private String paymentId;
}
