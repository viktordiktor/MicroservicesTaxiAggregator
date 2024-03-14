package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = ValidationList.PASSENGER_ID_REQUIRED)
    private UUID passengerId;
    @NotBlank(message = ValidationList.CURRENCY_REQUIRED)
    private String currency;
    @NotNull(message = ValidationList.AMOUNT_REQUIRED)
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    private BigDecimal amount;
}
