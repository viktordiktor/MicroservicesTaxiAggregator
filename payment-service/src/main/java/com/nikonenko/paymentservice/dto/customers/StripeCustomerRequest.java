package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.ErrorList;
import com.nikonenko.paymentservice.utils.PatternList;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeCustomerRequest {
    private String username;
    @Pattern(regexp = PatternList.PHONE_PATTERN, message = ErrorList.WRONG_PHONE_FORMAT)
    private String phone;
    private Long passengerId;
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private Long amount;
}
