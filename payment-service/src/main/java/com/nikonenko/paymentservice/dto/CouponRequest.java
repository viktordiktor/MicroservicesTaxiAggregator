package com.nikonenko.paymentservice.dto;

import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotNull;
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
public class CouponRequest {
    @NotNull(message = ValidationList.DURATION_REQUIRED)
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    private Long monthDuration;
    @NotNull(message = ValidationList.PERCENT_REQUIRED)
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    private BigDecimal percent;
}
