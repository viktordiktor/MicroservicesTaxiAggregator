package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class UnknownDriverException extends RuntimeException {
    public UnknownDriverException() {
        super(ErrorList.DRIVER_UNKNOWN.getValue());
    }
}
