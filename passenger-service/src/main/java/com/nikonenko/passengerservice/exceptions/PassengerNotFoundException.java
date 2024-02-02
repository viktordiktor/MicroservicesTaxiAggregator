package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ErrorList;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException() {
        super(ErrorList.NOT_FOUND.getValue());
    }
}
