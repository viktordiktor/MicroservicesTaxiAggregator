package com.nikonenko.rideservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "driver_id")
    private Long driverId;
    @Column(name = "passenger_id")
    private Long passengerId;
    @Column(name = "start_address")
    private String startAddress;
    @Column(name = "end_address")
    private String endAddress;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "charge_id")
    private String chargeId;
    @Column(name = "distance")
    private Double distance;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RideStatus status;
}