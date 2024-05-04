package com.nikonenko.rideservice.utils;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum ExceptionList {
    DRIVER_NOT_AVAILABLE("driver.not-available"),
    DRIVER_UNKNOWN("driver.unknown"),
    RIDE_NOT_FOUND("ride.not-found"),
    RIDE_IS_NOT_OPENED("ride.is-not-opened"),
    RIDE_IS_NOT_ACCEPTED("ride.is-not-accepted"),
    RIDE_IS_NOT_STARTED("ride.is-not-started"),
    RIDE_IS_NOT_FINISHED("ride.is-not-finished"),
    WRONG_PAGEABLE_PARAMETER("wrong-pageable-parameter"),
    WRONG_SORT_FIELD("wrong-sort-field"),
    WRONG_LAT_LNG_PARAMETER("wrong-lat-lng-parameter"),
    CHARGE_IS_NOT_SUCCESS("charge-is-not-success"),
    BAD_REQUEST_BY_RIDE("bad-request-by-ride"),
    RIDE_IS_ALREADY_STARTED("ride.is-already-started"),
    PAYMENT_SERVICE_NOT_AVAILABLE("payment-service-not-available"),;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
