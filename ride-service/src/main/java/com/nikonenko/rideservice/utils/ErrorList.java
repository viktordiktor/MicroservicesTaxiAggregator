package com.nikonenko.rideservice.utils;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum ErrorList {
    DRIVER_NOT_AVAILABLE("driver.not-available"),
    RIDE_NOT_FOUND("ride.not-found"),
    RIDE_IS_NOT_STARTED("ride.is-not-started"),
    WRONG_PAGEABLE_PARAMETER("wrong-pageable-parameter"),
    RIDE_IS_ALREADY_STARTED("ride.is-already-started");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
