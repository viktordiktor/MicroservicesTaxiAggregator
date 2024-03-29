package com.nikonenko.driverservice.controllers;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PageResponse<CarResponse> getAllCars(@RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "5") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortField) {
        return carService.getAllCars(pageNumber, pageSize, sortField);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CarResponse getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CarResponse editCar(@PathVariable Long id, @Valid @RequestBody CarRequest carRequest) {
        return carService.editCar(id, carRequest);
    }
}
