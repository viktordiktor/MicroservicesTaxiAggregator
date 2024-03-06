package com.nikonenko.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/passenger-service")
    public String passengerServiceFallback() {
        return "Passenger service is down right now. Please try again later.";
    }

    @GetMapping("/driver-service")
    public String driverServiceFallback() {
        return "Driver service is down right now. Please try again later.";
    }

    @GetMapping("/ride-service")
    public String rideServiceFallback() {
        return "Rides service is down right now. Please try again later.";
    }

    @GetMapping("/payment-service")
    public String paymentServiceFallback() {
        return "Payment service is down right now. Please try again later.";
    }
}
