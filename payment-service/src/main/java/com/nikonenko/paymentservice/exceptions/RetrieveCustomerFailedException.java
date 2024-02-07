package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class RetrieveCustomerFailedException extends StripeOperationFailedException {
    public RetrieveCustomerFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.RETRIEVE_CUSTOMER_FAILED.getValue(), message));
    }
}
