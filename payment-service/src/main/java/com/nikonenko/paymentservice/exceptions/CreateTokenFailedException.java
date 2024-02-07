package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreateTokenFailedException extends StripeOperationFailedException {
    public CreateTokenFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_TOKEN_FAILED.getValue(), message));
    }
}
