package com.nikonenko.passengerservice.utils;

import lombok.AllArgsConstructor;
import java.util.ResourceBundle;

@AllArgsConstructor
public enum ExceptionList {
    USERNAME_EXISTS("passenger.exists.username"),
    PHONE_EXISTS("passenger.exists.phone"),
    NOT_FOUND("passenger.notfound"),
    WRONG_PARAMETER("wrong.parameter");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
