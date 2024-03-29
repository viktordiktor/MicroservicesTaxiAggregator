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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rides")
public class RideController {
    private final RideService rideService;

    @GetMapping("/distance")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public CalculateDistanceResponse calculateDistance(@RequestParam(value = "startGeo") LatLng startGeo,
                                                       @RequestParam(value = "endGeo") LatLng endGeo) {
        return rideService.calculateDistance(startGeo, endGeo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRideRequest(@Valid @RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER', 'ROLE_ADMIN')")
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
    @PreAuthorize("(hasRole('ROLE_PASSENGER') && #passengerId == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    public PageResponse<RideResponse> getRidesByPassenger(@PathVariable UUID passengerId,
                                                           @RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "5") int pageSize,
                                                           @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByPassenger(passengerId, pageNumber, pageSize, sortField);
    }

    @GetMapping("/by-driver/{driverId}")
    @PreAuthorize("(hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    public PageResponse<RideResponse> getRidesByDriver(@PathVariable UUID driverId,
                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "id") String sortField) {
        return rideService.getRidesByDriver(driverId, pageNumber, pageSize, sortField);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasAnyRole('ROLE_PASSENGER', 'ROLE_ADMIN'))")
    public CloseRideResponse closeRide(@PathVariable String id) {
        return rideService.closeRide(id);
    }
}
