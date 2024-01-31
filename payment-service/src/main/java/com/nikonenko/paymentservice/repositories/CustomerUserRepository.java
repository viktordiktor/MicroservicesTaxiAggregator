package com.nikonenko.paymentservice.repositories;

import com.nikonenko.paymentservice.models.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {
    boolean existsByPassengerId(Long passengerId);
}
