package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.models.RideAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRideStatusRequest {
    private Long driverId;
    private String rideId;
    private RideAction rideAction;
}
