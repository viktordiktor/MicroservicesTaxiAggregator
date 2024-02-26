package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideIsNotFinishedException extends RuntimeException {
    public RideIsNotFinishedException() {
        super(ExceptionList.RIDE_IS_NOT_FINISHED.getValue());
    }
}
