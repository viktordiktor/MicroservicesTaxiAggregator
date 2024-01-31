package com.nikonenko.driverservice.services;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;

public interface CarService {
    PageResponse<CarResponse> getAllCars(int pageNumber, int pageSize, String sortField);

    CarResponse getCarById(Long id);

    CarResponse editCar(Long id, CarRequest carRequest);

    CarResponse createCar(CarRequest carRequest);

    void deleteCar(Long id);
}
