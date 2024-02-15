package com.nikonenko.paymentservice.repositories;

import com.nikonenko.paymentservice.models.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {
    boolean existsByPassengerId(Long passengerId);

    Optional<CustomerUser> findByPassengerId(Long passengerId);

    Optional<CustomerUser> findByCustomerId(String customerId);
}
