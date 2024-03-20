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
public class RatingToPassengerRequest {
    private UUID passengerId;
    private int rating;
    private String comment;
}

