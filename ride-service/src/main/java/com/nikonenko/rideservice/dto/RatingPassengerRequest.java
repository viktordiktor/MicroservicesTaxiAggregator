package com.nikonenko.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingPassengerRequest {
    private Long passengerId;
    private int rating;
    private String comment;
}

