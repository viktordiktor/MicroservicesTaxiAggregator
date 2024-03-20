package com.nikonenko.rideservice.repositories;

import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RideRepository extends MongoRepository<Ride, String> {
    Page<Ride> findAllByStatusIs(RideStatus rideStatus, Pageable pageable);

    Page<Ride> findAllByPassengerIdIs(UUID passengerId, Pageable pageable);

    Page<Ride> findAllByDriverIdIs(UUID driverId, Pageable pageable);
}
