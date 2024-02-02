package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ErrorList;
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
    @NotBlank(message = ErrorList.TOKEN_REQUIRED)
    private String stripeToken;
    @NotBlank(message = ErrorList.CURRENCY_REQUIRED)
    private String currency;
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    @NotNull(message = ErrorList.AMOUNT_REQUIRED)
    private Double amount;
}
