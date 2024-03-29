package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingFromDriverRequest;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface DriverService {
    PageResponse<DriverResponse> getAllDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse getDriverById(UUID id);

    DriverResponse createDriver(OAuth2User principal);

    DriverResponse editDriver(UUID id, DriverRequest driverRequest);

    void deleteDriver(UUID id);

    void acceptRide(String rideId, UUID driverId);

    void rejectRide(String rideId, UUID driverId);

    void startRide(String rideId, UUID driverId);

    void finishRide(String rideId, UUID driverId);

    void createReview(RatingToDriverRequest ratingRequest);

    DriverResponse addCarToDriver(UUID id, CarRequest carRequest);

    void sendReviewToPassenger(String rideId, RatingFromDriverRequest request);

    PageResponse<RideResponse> getDriverRides(UUID driverId, int pageNumber, int pageSize, String sortField);

    void deleteCar(UUID driverId);
}
