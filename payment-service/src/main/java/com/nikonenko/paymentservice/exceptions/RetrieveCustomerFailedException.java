package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class RetrieveCustomerFailedException extends StripeOperationFailedException {
    public RetrieveCustomerFailedException(String message) {
        super(ErrorList.RETRIEVE_CUSTOMER_FAILED + message);
    }
}
