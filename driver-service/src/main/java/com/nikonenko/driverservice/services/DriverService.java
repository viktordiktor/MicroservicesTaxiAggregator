package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingDriverRequest;

public interface DriverService {
    PageResponse<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse getDriverById(Long id);

    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse editDriver(Long id, DriverRequest driverRequest);

    void deleteDriver(Long id);

    void acceptRide(String rideId, Long driverId);

    void rejectRide(String rideId, Long driverId);

    void startRide(String rideId, Long driverId);

    void finishRide(String rideId, Long driverId);

    void createReview(RatingDriverRequest ratingRequest);

    DriverResponse addCarToDriver(Long id, CarRequest carRequest);
}
