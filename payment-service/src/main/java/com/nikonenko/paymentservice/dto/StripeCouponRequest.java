package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ErrorList;
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
public class StripeCouponRequest {
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private Long monthDuration;
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private BigDecimal percent;
}
