package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class UnknownDriverException extends RuntimeException {
    public UnknownDriverException() {
        super(ExceptionList.DRIVER_UNKNOWN.getValue());
    }
}
