package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException() {
        super(ErrorList.RIDE_NOT_FOUND.getValue());
    }
}
