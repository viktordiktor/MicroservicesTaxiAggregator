package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideIsNotStartedException extends RuntimeException {
    public RideIsNotStartedException() {
        super(ExceptionList.RIDE_IS_NOT_STARTED.getValue());
    }
}
