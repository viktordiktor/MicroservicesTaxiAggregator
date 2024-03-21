package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class KeycloakUserIsNotValid extends RuntimeException {
    public KeycloakUserIsNotValid() {
        super(ExceptionList.KEYCLOAK_USER_NOT_VALID.getValue());
    }
}