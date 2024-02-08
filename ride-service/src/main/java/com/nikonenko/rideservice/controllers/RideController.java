package com.nikonenko.rideservice.controllers;

import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.services.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public CalculateDistanceResponse calculateDistance(@Valid @RequestBody
                                                       CalculateDistanceRequest calculateDistanceRequest) {
        return rideService.calculateDistance(calculateDistanceRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRideRequest(@Valid @RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @GetMapping("/open")
    public PageResponse<RideResponse> getAvailableRides(@RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getOpenRides(pageNumber, pageSize, sortField);
    }

    @GetMapping("/{rideId}")
    public RideResponse getRideById(@PathVariable String rideId) {
        return rideService.getRideById(rideId);
    }

    @GetMapping("/by-passenger/{passengerId}")
    public PageResponse<RideResponse> getRidersByPassenger(@PathVariable Long passengerId,
                                                           @RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "5") int pageSize,
                                                           @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByPassenger(passengerId, pageNumber, pageSize, sortField);
    }

    @GetMapping("/by-driver/{driverId}")
    public PageResponse<RideResponse> getRidersByDriver(@PathVariable Long driverId,
                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByDriver(driverId, pageNumber, pageSize, sortField);
    }

    @DeleteMapping("/{rideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeRide(@PathVariable String rideId) {
        rideService.closeRide(rideId);
    }

    @PatchMapping("/accept/{rideId}/{driverId}")
    public RideResponse acceptRide(@PathVariable String rideId, @PathVariable Long driverId) {
        return rideService.acceptRide(rideId, driverId);
    }

    @PatchMapping("/reject/{rideId}/{driverId}")
    public RideResponse rejectRide(@PathVariable String rideId, @PathVariable Long driverId) {
        return rideService.rejectRide(rideId, driverId);
    }

    @PatchMapping("/start/{rideId}/{driverId}")
    public RideResponse startRide(@PathVariable String rideId, @PathVariable Long driverId) {
        return rideService.startRide(rideId, driverId);
    }

    @PatchMapping("/finish/{rideId}/{driverId}")
    public RideResponse finishRide(@PathVariable String rideId, @PathVariable Long driverId) {
        return rideService.finishRide(rideId, driverId);
    }
}
