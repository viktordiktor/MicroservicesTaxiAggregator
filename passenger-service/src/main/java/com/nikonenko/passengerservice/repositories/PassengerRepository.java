package com.nikonenko.passengerservice.repositories;

import com.nikonenko.passengerservice.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);
}
