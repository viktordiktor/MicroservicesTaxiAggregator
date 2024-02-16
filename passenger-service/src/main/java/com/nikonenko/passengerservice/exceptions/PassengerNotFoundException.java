package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException() {
        super(ExceptionList.PASSENGER_NOT_FOUND.getValue());
    }
}
