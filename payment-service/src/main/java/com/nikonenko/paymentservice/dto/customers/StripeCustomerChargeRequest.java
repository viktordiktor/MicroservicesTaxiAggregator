package com.nikonenko.paymentservice.dto.customers;

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
public class StripeCustomerChargeRequest {
    private Long passengerId;
    private String currency;
    @Positive(message = ErrorList.NEGATIVE_AMOUNT)
    private Long amount;
}
