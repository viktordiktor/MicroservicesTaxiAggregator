package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.utils.ValidationList;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequest {
    @NotNull(message = ValidationList.DRIVER_ID_REQUIRED)
    private Long driverId;
    @NotNull(message = ValidationList.PASSENGER_ID_REQUIRED)
    private Long passengerId;
    @NotNull(message = ValidationList.START_ADDRESS_REQUIRED)
    private String startAddress;
    @NotNull(message = ValidationList.END_ADDRESS_REQUIRED)
    private String endAddress;
    @NotNull(message = ValidationList.START_DATE_REQUIRED)
    private LocalDateTime startDate;
    @NotNull(message = ValidationList.DISTANCE_REQUIRED)
    private Double distance;
    @NotNull(message = ValidationList.PRICE_REQUIRED)
    private Double price;
}
