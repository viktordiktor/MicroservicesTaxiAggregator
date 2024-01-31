package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class UpdateCustomerFailedException extends StripeOperationFailedException {
    public UpdateCustomerFailedException(String message) {
        super(ErrorList.UPDATE_CUSTOMER_FAILED + message);
    }
}