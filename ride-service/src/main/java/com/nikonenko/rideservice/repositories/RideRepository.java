package com.nikonenko.rideservice.repositories;

import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RideStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RideRepository extends ReactiveMongoRepository<Ride, String> {
    Flux<Ride> findAllByStatusIs(RideStatus rideStatus, Pageable pageable);

    Flux<Ride> findAllByPassengerIdIs(UUID passengerId, Pageable pageable);

    Flux<Ride> findAllByDriverIdIs(UUID driverId, Pageable pageable);
}
