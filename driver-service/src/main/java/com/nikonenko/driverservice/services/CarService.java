package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import org.springframework.data.domain.Page;

public interface CarService {
    Page<CarResponse> getAllCars(int pageNumber, int pageSize, String sortField);

    CarResponse getCarById(Long id);

    CarResponse editCar(Long id, CarRequest carRequest);

    CarResponse createCar(CarRequest carRequest);

    void deleteCar(Long id);
}
