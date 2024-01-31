package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class ConfirmIntentFailedException extends StripeOperationFailedException {
    public ConfirmIntentFailedException(String message) {
        super(ErrorList.CONFIRM_INTENT_FAILED + message);
    }
}
