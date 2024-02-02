package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class RetrieveCustomerFailedException extends StripeOperationFailedException {
    public RetrieveCustomerFailedException(String message) {
        super(String.format("%s: %s", ErrorList.RETRIEVE_CUSTOMER_FAILED.getValue(), message));
    }
}
