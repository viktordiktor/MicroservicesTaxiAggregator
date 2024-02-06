package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreateChargeFailedException extends StripeOperationFailedException {
    public CreateChargeFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_CHARGE_FAILED.getValue(), message));
    }
}
