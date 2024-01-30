package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ErrorList;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super(ErrorList.USERNAME_EXISTS);
    }
}