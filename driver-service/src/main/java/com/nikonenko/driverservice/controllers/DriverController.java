package com.nikonenko.driverservice.controllers;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.services.DriverServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@RestControllerAdvice
public class DriverController {
    private final DriverServiceImpl driverService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<DriverResponse> getAllDrivers(@RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "5") int pageSize,
                                                      @RequestParam(defaultValue = "id") String sortField) {
        return driverService.getAllDrivers(pageNumber, pageSize, sortField);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createDriver(@Valid @RequestBody DriverRequest driverRequest) {
        return driverService.createDriver(driverRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DriverResponse getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DriverResponse editDriver(@PathVariable Long id, @Valid @RequestBody DriverRequest driverRequest) {
        return driverService.editDriver(id, driverRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PatchMapping("/{driverId}/accept/{rideId}")
    public void acceptRide(@PathVariable String rideId, @PathVariable Long driverId) {
        driverService.acceptRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/reject/{rideId}")
    public void rejectRide(@PathVariable String rideId, @PathVariable Long driverId) {
        driverService.rejectRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/start/{rideId}")
    public void startRide(@PathVariable String rideId, @PathVariable Long driverId) {
        driverService.startRide(rideId, driverId);
    }

    @PatchMapping("/{driverId}/finish/{rideId}")
    public void finishRide(@PathVariable String rideId, @PathVariable Long driverId) {
        driverService.finishRide(rideId, driverId);
    }

    @PostMapping("/car/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public DriverResponse addCarToDriver(@PathVariable Long driverId, @Valid @RequestBody CarRequest carRequest) {
        return driverService.addCarToDriver(driverId, carRequest);
    }
}
