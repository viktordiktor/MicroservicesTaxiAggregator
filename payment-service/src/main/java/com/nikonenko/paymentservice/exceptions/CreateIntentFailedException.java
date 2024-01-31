package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateIntentFailedException extends StripeOperationFailedException {
    public CreateIntentFailedException(String message) {
        super(ErrorList.CREATE_INTENT_FAILED + message);
    }
}