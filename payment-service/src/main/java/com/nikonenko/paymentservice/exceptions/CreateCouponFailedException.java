package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CreateCouponFailedException extends StripeOperationFailedException {
    public CreateCouponFailedException(String message) {
        super(String.format("%s: %s", ExceptionList.CREATE_COUPON_FAILED.getValue(), message));
    }
}
