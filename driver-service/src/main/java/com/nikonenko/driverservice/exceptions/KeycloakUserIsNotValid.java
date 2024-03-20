package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class KeycloakUserIsNotValid extends RuntimeException {
    public KeycloakUserIsNotValid() {
        super(ExceptionList.KEYCLOAK_USER_NOT_VALID.getValue());
    }
}
