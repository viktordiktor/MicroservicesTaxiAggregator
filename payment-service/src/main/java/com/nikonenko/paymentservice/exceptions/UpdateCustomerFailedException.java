package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class UpdateCustomerFailedException extends StripeOperationFailedException {
    public UpdateCustomerFailedException(String message) {
        super(String.format("%s: %s", ErrorList.UPDATE_CUSTOMER_FAILED.getValue(), message));
    }
}