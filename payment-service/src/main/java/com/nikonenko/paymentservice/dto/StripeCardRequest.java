package com.nikonenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeCardRequest {
    private String cardNumber;
    private Integer expirationMonth;
    private Integer expirationYear;
    private Integer cvc;
}
