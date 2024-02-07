package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreateIntentFailedException extends StripeOperationFailedException {
    public CreateIntentFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_INTENT_FAILED.getValue(), message));
    }
}