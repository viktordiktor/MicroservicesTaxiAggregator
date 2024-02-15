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
public class CustomerChargeResponse {
    private String id;
    private Long passengerId;
    private String currency;
    private BigDecimal amount;
    private boolean success;
}