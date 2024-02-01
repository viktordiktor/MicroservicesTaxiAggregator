package com.nikonenko.paymentservice.dto.customers;

import com.nikonenko.paymentservice.utils.ErrorList;
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
public class CustomerRideRequest {
    @Positive(message = ErrorList.NEGATIVE_VALUE)
    private Double rideLength;
    @PastOrPresent(message = ErrorList.WRONG_DATETIME_FORMAT)
    private LocalDateTime rideDate;
    private String coupon;
}
