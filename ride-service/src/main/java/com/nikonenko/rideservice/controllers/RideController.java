package com.nikonenko.rideservice.controllers;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.services.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<RideResponse> getAvailableRides(@RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getAvailableRides(pageNumber, pageSize, sortField);
    }

    @GetMapping("/by-passenger/{passengerId}")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<RideResponse> getRidersByPassenger(@PathVariable Long passengerId,
                                                             @RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "5") int pageSize,
                                                             @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByPassenger(passengerId, pageNumber, pageSize, sortField);
    }

    @GetMapping("/by-driver/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<RideResponse> getRidersByDriver(@PathVariable Long driverId,
                                                           @RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "5") int pageSize,
                                                           @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByDriver(driverId, pageNumber, pageSize, sortField);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRideRequest(@RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @PatchMapping("/accept/{rideId}/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse acceptRide(@PathVariable Long rideId, @PathVariable Long driverId) {
        return rideService.acceptRide(rideId, driverId);
    }

    @PatchMapping("/reject/{rideId}/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse rejectRide(@PathVariable Long rideId, @PathVariable Long driverId) {
        return rideService.rejectRide(rideId, driverId);
    }

    @PatchMapping("/start/{rideId}/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse startRide(@PathVariable Long rideId, @PathVariable Long driverId) {
        return rideService.startRide(rideId, driverId);
    }

    @PatchMapping("/finish/{rideId}/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse finishRide(@PathVariable Long rideId, @PathVariable Long driverId) {
        return rideService.finishRide(rideId, driverId);
    }
}
