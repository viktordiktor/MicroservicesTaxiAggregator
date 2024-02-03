package com.nikonenko.rideservice.controllers;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.CreateRideResponse;
import com.nikonenko.rideservice.services.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rides")
@RestControllerAdvice
public class RideController {
    private final RideService rideService;

    @GetMapping("/distance")
    @ResponseStatus(HttpStatus.OK)
    public CalculateDistanceResponse calculateDistance(@RequestBody CalculateDistanceRequest calculateDistanceRequest) {
        return rideService.calculateDistance(calculateDistanceRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRideResponse createRide(@RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }
}
