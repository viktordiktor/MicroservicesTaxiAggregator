package com.nikonenko.passengerservice.controllers;

import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingPassengerRequest;
import com.nikonenko.passengerservice.services.PassengerServiceImpl;
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
    private final PassengerServiceImpl passengerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponse getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponse editPassenger(@PathVariable Long id,
                                           @Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.editPassenger(id, passengerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }

    @PostMapping("/rating/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponse addRating(@PathVariable Long id,
                                       @Valid @RequestBody RatingPassengerRequest ratingRequest) {
        return passengerService.createReview(id, ratingRequest);
    }
}