package com.nikonenko.e2etests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCalculateRideRequest {
    private Double rideLength;
    private LocalDateTime rideDateTime;
    private String coupon;
}
