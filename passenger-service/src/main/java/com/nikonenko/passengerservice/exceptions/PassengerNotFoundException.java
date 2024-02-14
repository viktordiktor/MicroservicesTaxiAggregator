package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException() {
        super(ExceptionList.NOT_FOUND.getValue());
    }
}
