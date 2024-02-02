package com.nikonenko.paymentservice.dto.customers;

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
public class CustomerChargeRequest {
    @NotNull(message = ErrorList.PASSENGER_ID_REQUIRED)
    private Long passengerId;
    @NotBlank(message = ErrorList.CURRENCY_REQUIRED)
    private String currency;
    @NotNull(message = ErrorList.AMOUNT_REQUIRED)
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private Double amount;
}
