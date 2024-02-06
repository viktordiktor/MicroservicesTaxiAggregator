package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreateCustomerException extends StripeOperationFailedException {
    public CreateCustomerException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_CUSTOMER_FAILED.getValue(), message));
    }
}
