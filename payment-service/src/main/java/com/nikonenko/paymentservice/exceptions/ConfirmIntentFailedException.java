package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class ConfirmIntentFailedException extends StripeOperationFailedException {
    public ConfirmIntentFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CONFIRM_INTENT_FAILED.getValue(), message));
    }
}
