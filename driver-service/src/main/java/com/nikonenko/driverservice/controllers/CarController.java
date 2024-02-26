package com.nikonenko.driverservice.controllers;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
@RestControllerAdvice
public class CarController {
    private final CarService carService;

    @GetMapping
    public PageResponse<CarResponse> getAllCars(@RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "5") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortField) {
        return carService.getAllCars(pageNumber, pageSize, sortField);
    }

    @GetMapping("/{id}")
    public CarResponse getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PutMapping("/{id}")
    public CarResponse editCar(@PathVariable Long id, @Valid @RequestBody CarRequest carRequest) {
        return carService.editCar(id, carRequest);
    }
}
