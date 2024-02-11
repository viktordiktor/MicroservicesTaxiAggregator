package com.nikonenko.passengerservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerReviewRequest {
    private String rideId;
    @Min(value = 1)
    @Max(value = 5)
    private int rating;
    private String comment;
}
