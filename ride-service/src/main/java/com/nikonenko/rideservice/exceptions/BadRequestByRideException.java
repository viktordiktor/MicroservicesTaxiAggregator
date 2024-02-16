package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class BadRequestByRideException extends RuntimeException {
    public BadRequestByRideException(String message) {
        super(String.format("%s: %s", ExceptionList.BAD_REQUEST_BY_RIDE.getValue(), message));
    }
}
