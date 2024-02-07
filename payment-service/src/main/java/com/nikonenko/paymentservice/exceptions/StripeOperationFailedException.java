package com.nikonenko.paymentservice.exceptions;

public class StripeOperationFailedException extends RuntimeException {
    public StripeOperationFailedException(String message) {
        super(message);
    }
}
