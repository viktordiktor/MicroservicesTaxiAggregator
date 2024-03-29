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
    ACCESS_DENIED_BY_PASSENGER("access-denied-by-passenger"),
    CHARGE_IS_NOT_SUCCESS("charge-is-not-success"),
    NOT_FOUND_BY_PASSENGER("not-found-by-passenger"),
    KEYCLOAK_USER_NOT_VALID("keycloak-not-valid");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
