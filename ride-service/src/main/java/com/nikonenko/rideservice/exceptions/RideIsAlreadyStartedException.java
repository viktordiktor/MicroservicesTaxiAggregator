package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class RideIsAlreadyStartedException extends RuntimeException{
    public RideIsAlreadyStartedException() {
        super(ErrorList.RIDE_IS_ALREADY_STARTED.getValue());
    }
}
