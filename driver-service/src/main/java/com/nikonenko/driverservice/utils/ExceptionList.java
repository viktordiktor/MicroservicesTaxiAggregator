package com.nikonenko.driverservice.utils;

import lombok.AllArgsConstructor;
import java.util.ResourceBundle;

@AllArgsConstructor
public enum ExceptionList {
    USERNAME_EXISTS("username-exists"),
    PHONE_EXISTS("phone-exists"),
    DRIVER_NOT_FOUND("driver-not-found"),
    CAR_NOT_FOUND("car-not-found"),
    WRONG_PARAMETER("wrong-parameter"),
    NUMBER_EXISTS("number-exists"),
    DRIVER_NOT_AVAILABLE("driver-not-available"),
    BAD_REQUEST_BY_DRIVER("bad-request-by-driver"),
    DRIVER_NO_RIDES("driver-no-rides"),
    RIDE_SERVICE_NOT_AVAILABLE("ride-service-not-available");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}