package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.RatingDriverRequest;
import org.springframework.data.domain.Page;

public interface DriverService {
    Page<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse getDriverById(Long id);

    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse editDriver(Long id, DriverRequest driverRequest);

    void deleteDriver(Long id);

    DriverResponse createReview(Long id, RatingDriverRequest ratingRequest);
}
