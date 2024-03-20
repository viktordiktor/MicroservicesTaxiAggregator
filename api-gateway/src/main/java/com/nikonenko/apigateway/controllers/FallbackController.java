package com.nikonenko.apigateway.controllers;

import com.nikonenko.apigateway.utils.ExceptionList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/passenger-service")
    public String passengerServiceFallback() {
        return ExceptionList.PASSENGER_SERVICE_DOWN.getValue();
    }

    @GetMapping("/driver-service")
    public String driverServiceFallback() {
        return ExceptionList.DRIVER_SERVICE_DOWN.getValue();
    }

    @GetMapping("/ride-service")
    public String rideServiceFallback() {
        return ExceptionList.RIDE_SERVICE_DOWN.getValue();
    }

    @GetMapping("/payment-service")
    public String paymentServiceFallback() {
        return ExceptionList.PAYMENT_SERVICE_DOWN.getValue();
    }
}