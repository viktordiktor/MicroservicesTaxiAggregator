package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.ValidationList;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    @NotNull(message = ValidationList.LENGTH_REQUIRED)
    private Double rideLength;
    @PastOrPresent(message = ValidationList.WRONG_DATETIME_FORMAT)
    @NotNull(message = ValidationList.DATETIME_REQUIRED)
    private LocalDateTime rideDateTime;
    private String coupon;
}
