package com.nikonenko.passengerservice.dto.feign.ride;

import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.models.feign.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private String id;
    private UUID driverId;
    private UUID passengerId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String chargeId;
    private Double distance;
    private RideStatus status;
    private RidePaymentMethod paymentMethod;
    private String errorMessage;
    private CarResponse car;
}
