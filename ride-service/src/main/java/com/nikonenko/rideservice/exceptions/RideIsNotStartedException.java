package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class RideIsNotStartedException extends RuntimeException {
    public RideIsNotStartedException() {
        super(ErrorList.RIDE_IS_NOT_STARTED.getValue());
    }
}
