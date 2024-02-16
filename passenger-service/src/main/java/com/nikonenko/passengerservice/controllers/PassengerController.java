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
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/passengers")
@RestControllerAdvice
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public PageResponse<PassengerResponse> getAllPassengers(@RequestParam(defaultValue = "0") int pageNumber,
                                                            @RequestParam(defaultValue = "5") int pageSize,
                                                            @RequestParam(defaultValue = "id") String sortField) {
        return passengerService.getAllPassengers(pageNumber, pageSize, sortField);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse createPassenger(@Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.createPassenger(passengerRequest);
    }

    @PostMapping("/{passengerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRide(@PathVariable Long passengerId,
                                   @RequestBody RideByPassengerRequest rideByPassengerRequest) {
        return passengerService.createRideByPassenger(passengerId, rideByPassengerRequest);
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @PutMapping("/{id}")
    public PassengerResponse editPassenger(@PathVariable Long id,
                                           @Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.editPassenger(id, passengerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }

    @PostMapping("/rating/{rideId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRatingToDriver(@PathVariable String rideId,
                                  @Valid @RequestBody RatingFromPassengerRequest request) {
        passengerService.sendReviewToDriver(rideId, request);
    }

    @PostMapping("/{passengerId}/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@PathVariable Long passengerId, @Valid @RequestBody CustomerDataRequest dataRequest) {
        passengerService.createCustomerByPassenger(passengerId, dataRequest);
    }

    @DeleteMapping("/close/{rideId}")
    public CloseRideResponse closeRide(@PathVariable String rideId) {
        return passengerService.closeRide(rideId);
    }

    @GetMapping("/rides/{passengerId}")
    public PageResponse<RideResponse> getPassengerRides(@PathVariable Long passengerId) {
        return passengerService.getPassengerRides(passengerId);
    }
}