package com.nikonenko.e2etests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverResponse {
    private Long id;
    private String username;
    private String phone;
    private CarResponse car;
    private Set<RatingDriverResponse> ratingSet;
    private boolean available;
}
