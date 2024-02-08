package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class RideIsAlreadyStartedException extends RuntimeException{
    public RideIsAlreadyStartedException() {
        super(ExceptionList.RIDE_IS_ALREADY_STARTED.getValue());
    }
}
