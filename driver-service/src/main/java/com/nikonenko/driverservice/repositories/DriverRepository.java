package com.nikonenko.driverservice.repositories;

import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhone(String phone);

    boolean existsByUsername(String username);
    Optional<Driver> findByCar(Car car);
}
