package com.nikonenko.passengerservice.exceptions;


public class AccessDeniedByPassengerException extends RuntimeException {
    public AccessDeniedByPassengerException(String message) {
        super(message);
    }
}