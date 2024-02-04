package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class RideIsNotOpenedException extends RuntimeException {
    public RideIsNotOpenedException() {
        super(ErrorList.RIDE_IS_NOT_OPENED.getValue());
    }
}