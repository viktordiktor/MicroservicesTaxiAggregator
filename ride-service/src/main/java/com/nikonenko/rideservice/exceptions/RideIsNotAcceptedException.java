package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class RideIsNotAcceptedException extends RuntimeException {
    public RideIsNotAcceptedException() {
        super(ErrorList.RIDE_IS_NOT_ACCEPTED.getValue());
    }
}
