package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateChargeFailedException extends StripeOperationFailedException {
    public CreateChargeFailedException(String message) {
        super(String.format("%s: %s", ErrorList.CREATE_CHARGE_FAILED, message));
    }
}
