package com.nikonenko.passengerservice.dto.feign.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCalculateRideResponse {
    private BigDecimal price;
    private Double rideLength;
    private LocalDateTime rideDateTime;
    private String coupon;
    private String errorMessage;
}
