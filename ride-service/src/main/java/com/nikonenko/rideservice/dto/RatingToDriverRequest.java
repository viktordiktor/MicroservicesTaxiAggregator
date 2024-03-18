package com.nikonenko.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingToDriverRequest {
    private UUID driverId;
    private int rating;
    private String comment;
}
