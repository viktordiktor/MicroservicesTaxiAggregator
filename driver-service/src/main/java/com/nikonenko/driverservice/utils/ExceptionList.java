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
    WRONG_SORT_FIELD("wrong-sort-field"),
    NUMBER_EXISTS("number-exists"),
    DRIVER_NOT_AVAILABLE("driver-not-available"),
    BAD_REQUEST_BY_DRIVER("bad-request-by-driver"),
    DRIVER_NO_RIDES("driver-no-rides"),
    DRIVER_NO_CAR("driver-no-car"),
    KEYCLOAK_USER_NOT_VALID("keycloak-user-not-valid");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}