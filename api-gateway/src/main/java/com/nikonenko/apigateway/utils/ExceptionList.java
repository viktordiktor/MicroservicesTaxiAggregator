package com.nikonenko.apigateway.utils;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum ExceptionList {
    PASSENGER_SERVICE_DOWN("passenger-service-down"),
    DRIVER_SERVICE_DOWN("driver-service-down"),
    RIDE_SERVICE_DOWN("ride-service-down"),
    PAYMENT_SERVICE_DOWN("payment-service-down");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}