package com.nikonenko.passengerservice.services;

import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;

import java.util.List;

public interface PassengerService {
    List<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField);
    PassengerResponse getPassengerById(Long id);
    PassengerResponse createPassenger(PassengerRequest passengerRequest);
    PassengerResponse editPassenger(Long id, PassengerRequest passengerRequest);
    void deletePassenger(Long id);
}
