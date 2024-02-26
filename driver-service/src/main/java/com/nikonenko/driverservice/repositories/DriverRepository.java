package com.nikonenko.driverservice.repositories;

import com.nikonenko.driverservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhone(String phone);

    boolean existsByUsername(String username);
}
