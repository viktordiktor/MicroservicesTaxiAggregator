package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.dto.feign.drivers.CarResponse;
import com.nikonenko.rideservice.models.RideAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRideStatusRequest {
    private UUID driverId;
    private String rideId;
    private RideAction rideAction;
    private CarResponse car;
}
