package com.nikonenko.passengerservice.dto.feign.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerChargeResponse {
    private String id;
    private UUID passengerId;
    private String currency;
    private BigDecimal amount;
    private boolean success;
    private String errorMessage;
}
