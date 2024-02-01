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
public class CustomerRideRequest {
    private Double rideLength;
    private LocalDateTime rideDate;
    private String coupon;
}
