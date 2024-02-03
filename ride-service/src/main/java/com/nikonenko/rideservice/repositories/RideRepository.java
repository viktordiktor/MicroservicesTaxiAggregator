package com.nikonenko.rideservice.repositories;

import com.nikonenko.rideservice.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
}
