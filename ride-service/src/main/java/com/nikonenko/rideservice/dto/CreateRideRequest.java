package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.utils.ValidationList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequest {
    @NotNull(message = ValidationList.PASSENGER_ID_REQUIRED)
    private UUID passengerId;
    @NotBlank(message = ValidationList.START_ADDRESS_REQUIRED)
    private String startAddress;
    @NotBlank(message = ValidationList.END_ADDRESS_REQUIRED)
    private String endAddress;
    @NotNull(message = ValidationList.DISTANCE_REQUIRED)
    @Positive(message = ValidationList.NEGATIVE_VALUE)
    private Double distance;
    private String chargeId;
}
