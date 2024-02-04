package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequest {
    @NotNull(message = ValidationList.PASSENGER_ID_REQUIRED)
    private Long passengerId;
    @NotBlank(message = ValidationList.START_ADDRESS_REQUIRED)
    private String startAddress;
    @NotBlank(message = ValidationList.END_ADDRESS_REQUIRED)
    private String endAddress;
    @NotNull(message = ValidationList.DISTANCE_REQUIRED)
    private Double distance;
    @NotNull(message = ValidationList.PRICE_REQUIRED)
    private Double price;
}
