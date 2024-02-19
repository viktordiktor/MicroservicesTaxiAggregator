package com.nikonenko.rideservice.models;

import com.nikonenko.rideservice.dto.feign.drivers.CarResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(value = "rides")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Ride {
    @Id
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
