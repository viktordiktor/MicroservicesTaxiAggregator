package com.nikonenko.driverservice.repositories;

import com.nikonenko.driverservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    boolean existsByPhone(String phone);

    boolean existsByUsername(String username);
}
