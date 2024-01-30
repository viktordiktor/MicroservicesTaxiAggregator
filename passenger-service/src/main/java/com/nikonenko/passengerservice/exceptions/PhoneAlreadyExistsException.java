package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ErrorList;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException() {
        super(ErrorList.PHONE_EXISTS);
    }
}
