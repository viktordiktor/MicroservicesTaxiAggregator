package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class RefundFailedException extends StripeOperationFailedException {
    public RefundFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.REFUND_FAILED.getValue(), message));
    }
}
