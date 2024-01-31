package com.nikonenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeCouponResponse {
    private String id;
    private Long monthDuration;
    private BigDecimal percent;
}
