package com.nikonenko.driverservice.dto;

import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.RatingDriver;
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
    private String creditCard;
    private Set<Car> cars;
    private Set<RatingDriver> ratingSet;
}
