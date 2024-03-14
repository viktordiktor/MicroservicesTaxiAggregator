package com.nikonenko.e2etests.dto;

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
public class CustomerChargeRequest {
    private UUID passengerId;
    private String currency;
    private BigDecimal amount;
}
