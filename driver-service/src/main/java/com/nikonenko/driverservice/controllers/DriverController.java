package com.nikonenko.driverservice.controllers;

import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.services.DriverServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/driver")
@RestControllerAdvice
public class DriverController {

    private final DriverServiceImpl driverService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<DriverResponse> getAllPassengers(@RequestParam(defaultValue = "0") int pageNumber,
                                                 @RequestParam(defaultValue = "5") int pageSize,
                                                 @RequestParam(defaultValue = "id") String sortField) {
        return driverService.getAllDrivers(pageNumber, pageSize, sortField);
    }
}
