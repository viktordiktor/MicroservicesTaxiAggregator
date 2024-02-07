package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class RetrieveChargeFailedException extends StripeOperationFailedException {
    public RetrieveChargeFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.RETRIEVE_CHARGE_FAILED.getValue(), message));
    }
}
