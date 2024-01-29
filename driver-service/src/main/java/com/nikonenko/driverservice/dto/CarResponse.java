package com.nikonenko.driverservice.dto;

import com.nikonenko.driverservice.models.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private Long id;
    private Driver driver;
    private String number;
    private String model;
    private String color;
}
