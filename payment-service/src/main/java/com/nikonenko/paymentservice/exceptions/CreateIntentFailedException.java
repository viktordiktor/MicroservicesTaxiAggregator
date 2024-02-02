package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateIntentFailedException extends StripeOperationFailedException {
    public CreateIntentFailedException(String message) {
        super(String.format("%s: %s", ErrorList.CREATE_INTENT_FAILED.getValue(), message));
    }
}