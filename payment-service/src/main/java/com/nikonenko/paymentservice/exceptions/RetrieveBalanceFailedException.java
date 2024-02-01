package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class RetrieveBalanceFailedException extends StripeOperationFailedException {
    public RetrieveBalanceFailedException(String message) {
        super(String.format("%s: %s", ErrorList.RETRIEVE_BALANCE_FAILED, message));
    }
}
