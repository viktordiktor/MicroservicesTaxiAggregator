package com.nikonenko.passengerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerResponse {
    private Long id;
    private String username;
    private String phone;
    private Set<RatingPassengerResponse> ratingSet;
}