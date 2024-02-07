package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.PatternList;
import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotBlank(message = ValidationList.CARD_REQUIRED)
    @Pattern(regexp = PatternList.CARD_PATTERN, message = ValidationList.WRONG_CARD_FORMAT)
    private String cardNumber;
    @NotNull(message = ValidationList.EXP_MONTH_REQUIRED)
    @Range(min = 1, max = 12, message = ValidationList.UNEXPECTED_MONTH_RANGE)
    private Integer expirationMonth;
    @NotNull(message = ValidationList.EXP_YEAR_REQUIRED)
    @Range(min = 0, max = 99, message = ValidationList.UNEXPECTED_YEAR_RANGE)
    private Integer expirationYear;
    @NotNull(message = ValidationList.CVC_REQUIRED)
    @Range(min = 100, max = 999, message = ValidationList.WRONG_CVC_FORMAT)
    private Integer cvc;
}
