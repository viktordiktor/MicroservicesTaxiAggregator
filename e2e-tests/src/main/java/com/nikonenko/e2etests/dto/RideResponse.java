package com.nikonenko.e2etests.dto;

import com.nikonenko.e2etests.models.RidePaymentMethod;
import com.nikonenko.e2etests.models.RideStatus;
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
    private String errorMessage;
    private CarResponse car;
}
