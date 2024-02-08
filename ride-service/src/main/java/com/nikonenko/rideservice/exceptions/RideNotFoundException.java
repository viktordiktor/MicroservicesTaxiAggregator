package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException() {
        super(ExceptionList.RIDE_NOT_FOUND.getValue());
    }
}
