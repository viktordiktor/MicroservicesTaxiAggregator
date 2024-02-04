package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class RetrieveChargeFailedException extends StripeOperationFailedException {
    public RetrieveChargeFailedException(String message) {
        super(String.format("%s: %s", ErrorList.RETRIEVE_CHARGE_FAILED.getValue(), message));
    }
}
