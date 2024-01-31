package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreatePaymentFailedException extends StripeOperationFailedException {
    public CreatePaymentFailedException(String message) {
        super(ErrorList.CREATE_PAYMENT_FAILED + message);
    }
}
