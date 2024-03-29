package com.nikonenko.driverservice.controllers;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingFromDriverRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_DRIVER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<DriverResponse> getAllDrivers(@RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "5") int pageSize,
                                                      @RequestParam(defaultValue = "id") String sortField) {
        return driverService.getAllDrivers(pageNumber, pageSize, sortField);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createDriver(@AuthenticationPrincipal OAuth2User principal) {
        return driverService.createDriver(principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_DRIVER') && #id == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    public DriverResponse getDriverById(@PathVariable UUID id) {
        return driverService.getDriverById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #id == authentication.principal.id")
    public DriverResponse editDriver(@PathVariable UUID id, @Valid @RequestBody DriverRequest driverRequest) {
        return driverService.editDriver(id, driverRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_DRIVER') && #id == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
    }

    @DeleteMapping("/car/{driverId}")
    @PreAuthorize("(hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable UUID driverId) {
        driverService.deleteCar(driverId);
    }

    @PatchMapping("/{driverId}/accept/{rideId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id")
    public void acceptRide(@PathVariable String rideId, @PathVariable UUID driverId) {
        driverService.acceptRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/reject/{rideId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id")
    public void rejectRide(@PathVariable String rideId, @PathVariable UUID driverId) {
        driverService.rejectRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/start/{rideId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id")
    public void startRide(@PathVariable String rideId, @PathVariable UUID driverId) {
        driverService.startRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/finish/{rideId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id")
    public void finishRide(@PathVariable String rideId, @PathVariable UUID driverId) {
        driverService.finishRide(rideId, driverId);
    }

    @PostMapping("/car/{driverId}")
    @PreAuthorize("hasRole('ROLE_DRIVER') && #driverId == authentication.principal.id")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse addCarToDriver(@PathVariable UUID driverId, @Valid @RequestBody CarRequest carRequest) {
        return driverService.addCarToDriver(driverId, carRequest);
    }

    @PostMapping("/rating/{rideId}")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRatingToPassenger(@PathVariable String rideId,
                                     @Valid @RequestBody RatingFromDriverRequest request) {
        driverService.sendReviewToPassenger(rideId, request);
    }

    @GetMapping("/rides/{driverId}")
    public PageResponse<RideResponse> getDriverRides(@PathVariable UUID driverId,
                                                     @RequestParam(defaultValue = "0") int pageNumber,
                                                     @RequestParam(defaultValue = "5") int pageSize,
                                                     @RequestParam(defaultValue = "id") String sortField) {
        return driverService.getDriverRides(driverId, pageNumber, pageSize, sortField);
    }
}
