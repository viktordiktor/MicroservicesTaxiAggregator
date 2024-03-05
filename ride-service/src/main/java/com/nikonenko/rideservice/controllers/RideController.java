package com.nikonenko.rideservice.controllers;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.services.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public CalculateDistanceResponse calculateDistance(@RequestParam(value = "startGeo") LatLng startGeo,
                                                       @RequestParam(value = "endGeo") LatLng endGeo) {
        return rideService.calculateDistance(startGeo, endGeo);
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
    public PageResponse<RideResponse> getRidesByPassenger(@PathVariable Long passengerId,
                                                           @RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "5") int pageSize,
                                                           @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByPassenger(passengerId, pageNumber, pageSize, sortField);
    }

    @GetMapping("/by-driver/{driverId}")
    public PageResponse<RideResponse> getRidesByDriver(@PathVariable Long driverId,
                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByDriver(driverId, pageNumber, pageSize, sortField);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CloseRideResponse closeRide(@PathVariable String id) {
        return rideService.closeRide(id);
    }
}
