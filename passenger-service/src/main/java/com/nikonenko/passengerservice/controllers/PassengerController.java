package com.nikonenko.passengerservice.controllers;

import com.nikonenko.passengerservice.dto.CustomerDataRequest;
import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingFromPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER', 'ROLE_ADMIN')")
    public PageResponse<PassengerResponse> getAllPassengers(@RequestParam(defaultValue = "0") int pageNumber,
                                                            @RequestParam(defaultValue = "5") int pageSize,
                                                            @RequestParam(defaultValue = "id") String sortField) {
        return passengerService.getAllPassengers(pageNumber, pageSize, sortField);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse createPassenger(@AuthenticationPrincipal OAuth2User principal) {
        return passengerService.createPassenger(principal);
    }

    @PostMapping("/{passengerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRide(@PathVariable UUID passengerId,
                                   @RequestBody RideByPassengerRequest rideByPassengerRequest) {
        return passengerService.createRideByPassenger(passengerId, rideByPassengerRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_PASSENGER') && #id == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    public PassengerResponse getPassengerById(@PathVariable UUID id) {
        return passengerService.getPassengerById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_PASSENGER') && #id == authentication.principal.id")
    public PassengerResponse editPassenger(@PathVariable UUID id,
                                           @Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.editPassenger(id, passengerRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_PASSENGER') && #id == authentication.principal.id) || hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable UUID id) {
        passengerService.deletePassenger(id);
    }

    @PostMapping("/rating/{rideId}")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRatingToDriver(@PathVariable String rideId,
                                  @Valid @RequestBody RatingFromPassengerRequest request) {
        passengerService.sendReviewToDriver(rideId, request);
    }

    @PostMapping("/{passengerId}/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@PathVariable UUID passengerId, @Valid @RequestBody CustomerDataRequest dataRequest) {
        passengerService.createCustomerByPassenger(passengerId, dataRequest);
    }

    @DeleteMapping("/close/{rideId}")
    public CloseRideResponse closeRide(@PathVariable String rideId) {
        return passengerService.closeRide(rideId);
    }

    @GetMapping("/rides/{passengerId}")
    public PageResponse<RideResponse> getPassengerRides(@PathVariable UUID passengerId) {
        return passengerService.getPassengerRides(passengerId);
    }
}