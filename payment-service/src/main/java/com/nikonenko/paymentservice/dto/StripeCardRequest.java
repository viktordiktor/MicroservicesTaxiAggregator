package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ErrorList;
import com.nikonenko.paymentservice.utils.PatternList;
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
public class StripeCardRequest {
    @Pattern(regexp = PatternList.CARD_PATTERN)
    private String cardNumber;
    @Range(min = 1, max = 12, message = ErrorList.UNEXPECTED_MONTH_RANGE)
    private String expirationMonth;
    @Range(min = 2000, max = 2100, message = ErrorList.UNEXPECTED_YEAR_RANGE)
    private Integer expirationYear;
    @Pattern(regexp = PatternList.CVC_PATTERN)
    private Integer cvc;
}
