package com.nikonenko.driverservice.repositories;

import com.nikonenko.driverservice.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByNumber(String number);
}
