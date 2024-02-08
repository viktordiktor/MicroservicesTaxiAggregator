package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideIsNotAcceptedException extends RuntimeException {
    public RideIsNotAcceptedException() {
        super(ExceptionList.RIDE_IS_NOT_ACCEPTED.getValue());
    }
}
