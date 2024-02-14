package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class NotFoundByPassengerException extends RuntimeException {
    public NotFoundByPassengerException() {
        super(ExceptionList.NOT_FOUND_BY_PASSENGER.getValue());
    }
}
