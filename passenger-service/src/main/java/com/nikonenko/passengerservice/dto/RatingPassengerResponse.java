package com.nikonenko.passengerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingPassengerResponse {
    private Long id;
    private int rating;
    private String comment;
}
