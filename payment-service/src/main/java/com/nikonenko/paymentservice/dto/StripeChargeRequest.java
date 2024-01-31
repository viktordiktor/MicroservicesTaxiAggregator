package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ErrorList;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeChargeRequest {
    private String stripeToken;
    private String currency;
    @Positive(message = ErrorList.NEGATIVE_AMOUNT)
    private Double amount;
}
