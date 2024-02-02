package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeRequest {
    @NotBlank(message = ValidationList.TOKEN_REQUIRED)
    private String stripeToken;
    @NotBlank(message = ValidationList.CURRENCY_REQUIRED)
    private String currency;
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    @NotNull(message = ValidationList.AMOUNT_REQUIRED)
    private Double amount;
}
