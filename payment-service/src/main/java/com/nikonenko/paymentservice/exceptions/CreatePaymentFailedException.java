package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreatePaymentFailedException extends StripeOperationFailedException {
    public CreatePaymentFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_PAYMENT_FAILED.getValue(), message));
    }
}
