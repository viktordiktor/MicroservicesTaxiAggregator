package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class UpdateCustomerFailedException extends StripeOperationFailedException {
    public UpdateCustomerFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.UPDATE_CUSTOMER_FAILED.getValue(), message));
    }
}