package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class RetrieveCouponFailedException extends StripeOperationFailedException {
    public RetrieveCouponFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.RETRIEVE_COUPON_FAILED.getValue(), message));
    }
}
