package com.nikonenko.passengerservice.services;

import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService{

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField) {
        List<Passenger> page = passengerRepository.findAll();
        return modelMapper.map(page, new TypeToken<List<PassengerResponse>>() {}.getType());
    }

    @Override
    public PassengerResponse getPassengerById(Long id) {
        return modelMapper.map(getPassenger(id), PassengerResponse.class);
    }

    @Override
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        checkPassengerExists(passengerRequest);
        Passenger passenger = modelMapper.map(passengerRequest, Passenger.class);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return modelMapper.map(savedPassenger, PassengerResponse.class);
    }

    @Override
    public PassengerResponse editPassenger(Long id, PassengerRequest passengerRequest) {
        checkPassengerExists(passengerRequest);
        Passenger editingPassenger = getPassenger(id);
        modelMapper.map(passengerRequest, editingPassenger);
        passengerRepository.save(editingPassenger);
        return modelMapper.map(editingPassenger, PassengerResponse.class);
    }

    @Override
    public void deletePassenger(Long id) {
        passengerRepository.delete(getPassenger(id));
    }


    public Passenger getPassenger(Long id) {
        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        if (optionalPassenger.isEmpty()) {
            throw new PassengerNotFoundException();
        }
        return optionalPassenger.get();
    }

    public void checkPassengerExists(PassengerRequest passengerRequest) {
        if (passengerRepository.existsByPhone(passengerRequest.getPhone())) {
            throw new PhoneAlreadyExistsException();
        }
        if (passengerRepository.existsByUsername(passengerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
    }
}