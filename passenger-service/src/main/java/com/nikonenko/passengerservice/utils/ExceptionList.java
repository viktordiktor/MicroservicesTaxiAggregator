package com.nikonenko.passengerservice.utils;

import lombok.AllArgsConstructor;
import java.util.ResourceBundle;

@AllArgsConstructor
public enum ExceptionList {
    USERNAME_EXISTS("passenger-exists-username"),
    PHONE_EXISTS("passenger-exists-phone"),
    PASSENGER_NOT_FOUND("passenger-not-found"),
    WRONG_PARAMETER("wrong-parameter"),
    WRONG_SORT_FIELD("wrong-sort-field"),
    BAD_REQUEST_BY_PASSENGER("bad-request-by-passenger"),
    RIDE_SERVICE_NOT_AVAILABLE("ride-service-not-available"),
    PAYMENT_SERVICE_NOT_AVAILABLE("payment-service-not-available"),
    CHARGE_IS_NOT_SUCCESS("charge-is-not-success"),
    NOT_FOUND_BY_PASSENGER("not-found-by-passenger"),
    KEYCLOAK_USER_NOT_VALID("keycloak-not-valid");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
