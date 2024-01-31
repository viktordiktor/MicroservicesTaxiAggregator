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
    @Pattern(regexp = PatternList.CARD_PATTERN, message = ErrorList.WRONG_CARD_FORMAT)
    private String cardNumber;
    @Range(min = 1, max = 12, message = ErrorList.UNEXPECTED_MONTH_RANGE)
    private String expirationMonth;
    @Range(min = 0, max = 99, message = ErrorList.UNEXPECTED_YEAR_RANGE)
    private Integer expirationYear;
    @Range(min = 100, max = 999, message = ErrorList.WRONG_CVC_FORMAT)
    private Integer cvc;
}
