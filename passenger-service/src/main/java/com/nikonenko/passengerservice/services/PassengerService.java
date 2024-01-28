package com.nikonenko.passengerservice.services;

import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingPassengerRequest;
import org.springframework.data.domain.Page;

public interface PassengerService {
    Page<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField);
    PassengerResponse getPassengerById(Long id);
    PassengerResponse createPassenger(PassengerRequest passengerRequest);
    PassengerResponse editPassenger(Long id, PassengerRequest passengerRequest);
    void deletePassenger(Long id);
    PassengerResponse createReview(Long id, RatingPassengerRequest ratingRequest);
}
