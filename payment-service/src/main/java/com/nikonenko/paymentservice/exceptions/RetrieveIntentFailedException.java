package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class RetrieveIntentFailedException extends StripeOperationFailedException {
    public RetrieveIntentFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.RETRIEVE_INTENT_FAILED.getValue(), message));
    }
}
