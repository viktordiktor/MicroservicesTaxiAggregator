package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.DriverResponse;
import org.springframework.data.domain.Page;

public interface DriverService {
    Page<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField);
}
