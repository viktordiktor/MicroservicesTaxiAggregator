package com.nikonenko.rideservice.dto;

import com.nikonenko.rideservice.dto.feign.drivers.CarResponse;
import com.nikonenko.rideservice.models.RidePaymentMethod;
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
    private String id;
    private Long driverId;
    private Long passengerId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String chargeId;
    private Double distance;
    private RideStatus status;
    private RidePaymentMethod paymentMethod;
    private CarResponse car;
}
