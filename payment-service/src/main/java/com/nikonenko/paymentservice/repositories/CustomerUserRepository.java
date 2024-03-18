package com.nikonenko.paymentservice.repositories;

import com.nikonenko.paymentservice.models.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {
    boolean existsByPassengerId(UUID passengerId);

    Optional<CustomerUser> findByPassengerId(UUID passengerId);

    Optional<CustomerUser> findByCustomerId(String customerId);
}
