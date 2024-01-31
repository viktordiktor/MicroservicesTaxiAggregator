package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException() {
        super(ErrorList.PHONE_EXISTS);
    }
}
