package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateCustomerException extends StripeOperationFailedException {
    public CreateCustomerException(String message) {
        super(String.format("%s: %s", ErrorList.CREATE_CUSTOMER_FAILED, message));
    }
}
