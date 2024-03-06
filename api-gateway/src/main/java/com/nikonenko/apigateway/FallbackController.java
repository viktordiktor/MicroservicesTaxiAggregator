package com.nikonenko.apigateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/passenger-service")
    public String passengerServiceFallback() {
        return "Passenger service is down right now. Please try again later.";
    }

    @RequestMapping("/fallback/driver-service")
    public String driverServiceFallback() {
        return "Driver service is down right now. Please try again later.";
    }

    @RequestMapping("/fallback/ride-service")
    public String rideServiceFallback() {
        return "Rides service is down right now. Please try again later.";
    }

    @RequestMapping("/fallback/payment-service")
    public String paymentServiceFallback() {
        return "Payment service is down right now. Please try again later.";
    }
}
