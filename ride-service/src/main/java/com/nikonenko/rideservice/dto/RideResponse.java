package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.models.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double price;
    private Double distance;
    private RideStatus status;
}
