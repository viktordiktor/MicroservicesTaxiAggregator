package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideIsNotOpenedException extends RuntimeException {
    public RideIsNotOpenedException() {
        super(ExceptionList.RIDE_IS_NOT_OPENED.getValue());
    }
}