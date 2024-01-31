package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class GenerateTokenFailedException extends StripeOperationFailedException {
    public GenerateTokenFailedException(String message) {
        super(ErrorList.GENERATE_TOKEN_FAILED + message);
    }
}
