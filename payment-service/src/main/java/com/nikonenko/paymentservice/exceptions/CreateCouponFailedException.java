package com.nikonenko.paymentservice.exceptions;

public class CreateCouponFailedException extends StripeOperationFailedException {
    public CreateCouponFailedException(String message) {
        super(message);
    }
}
