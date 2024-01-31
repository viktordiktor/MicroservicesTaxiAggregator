package com.nikonenko.paymentservice.exceptions;

public class RetrieveCouponFailedException extends StripeOperationFailedException {
    public RetrieveCouponFailedException(String message) {
        super(message);
    }
}
