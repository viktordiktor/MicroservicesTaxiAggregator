package com.nikonenko.passengerservice.dto.feign.ride;

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
    private String number;
    private String model;
    private String color;
}
