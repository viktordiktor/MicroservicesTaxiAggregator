package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.ErrorList;
import com.nikonenko.paymentservice.utils.PatternList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CustomerCreationRequest {
    @NotBlank(message = ErrorList.USERNAME_REQUIRED)
    private String username;
    @NotBlank(message = ErrorList.PHONE_REQUIRED)
    @Pattern(regexp = PatternList.PHONE_PATTERN, message = ErrorList.WRONG_PHONE_FORMAT)
    private String phone;
    @NotNull(message = ErrorList.PASSENGER_ID_REQUIRED)
    private Long passengerId;
    @NotNull(message = ErrorList.AMOUNT_REQUIRED)
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private Long amount;
}
