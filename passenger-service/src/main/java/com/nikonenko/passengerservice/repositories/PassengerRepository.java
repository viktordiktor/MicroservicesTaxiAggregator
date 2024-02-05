package com.nikonenko.passengerservice.repositories;

import com.nikonenko.passengerservice.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);
}
