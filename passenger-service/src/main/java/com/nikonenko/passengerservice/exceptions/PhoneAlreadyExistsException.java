package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException() {
        super(ExceptionList.PHONE_EXISTS.getValue());
    }
}
