package com.nikonenko.e2etests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequest {
    private Long passengerId;
    private String startAddress;
    private String endAddress;
    private Double distance;
    private String chargeId;
}
