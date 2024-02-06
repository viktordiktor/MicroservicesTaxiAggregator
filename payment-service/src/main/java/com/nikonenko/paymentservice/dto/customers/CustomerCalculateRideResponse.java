package com.nikonenko.paymentservice.dto.customers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCalculateRideResponse {
    private Double price;
    private Double rideLength;
    private LocalDateTime rideDateTime;
    private String coupon;
}