package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateTokenFailedException extends StripeOperationFailedException {
    public CreateTokenFailedException(String message) {
        super(String.format("%s: %s", ErrorList.CREATE_TOKEN_FAILED.getValue(), message));
    }
}
