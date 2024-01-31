package com.nikonenko.driverservice.dto;

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
    private Set<CarResponse> cars;
    private Set<RatingDriverResponse> ratingSet;
}
