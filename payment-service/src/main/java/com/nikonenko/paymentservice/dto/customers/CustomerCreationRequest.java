package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.PatternList;
import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreationRequest {
    @NotBlank(message = ValidationList.USERNAME_REQUIRED)
    private String username;
    @NotBlank(message = ValidationList.PHONE_REQUIRED)
    @Pattern(regexp = PatternList.PHONE_PATTERN, message = ValidationList.WRONG_PHONE_FORMAT)
    private String phone;
    @NotNull(message = ValidationList.PASSENGER_ID_REQUIRED)
    private Long passengerId;
    @NotNull(message = ValidationList.AMOUNT_REQUIRED)
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    private BigDecimal amount;
}
