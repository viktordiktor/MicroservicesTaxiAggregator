package com.nikonenko.e2etests.dto;

import com.nikonenko.e2etests.models.RideAction;
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
    private CarResponse car;
}
