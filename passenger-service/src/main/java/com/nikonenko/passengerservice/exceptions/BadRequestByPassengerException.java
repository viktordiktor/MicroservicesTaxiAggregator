package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class BadRequestByPassengerException extends RuntimeException {
    public BadRequestByPassengerException(String message) {
        super(String.format("%s: %s", ExceptionList.BAD_REQUEST_BY_PASSENGER.getValue(), message));
    }
}
