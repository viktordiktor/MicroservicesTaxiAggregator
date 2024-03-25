package com.nikonenko.apigateway.controllers;

import com.nikonenko.apigateway.utils.ExceptionList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping(value = "/passenger-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public String passengerServiceFallback() {
        return ExceptionList.PASSENGER_SERVICE_DOWN.getValue();
    }

    @RequestMapping(value = "/driver-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public String driverServiceFallback() {
        return ExceptionList.DRIVER_SERVICE_DOWN.getValue();
    }

    @RequestMapping(value = "/ride-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public String rideServiceFallback() {
        return ExceptionList.RIDE_SERVICE_DOWN.getValue();
    }

    @RequestMapping(value = "/payment-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public String paymentServiceFallback() {
        return ExceptionList.PAYMENT_SERVICE_DOWN.getValue();
    }
}