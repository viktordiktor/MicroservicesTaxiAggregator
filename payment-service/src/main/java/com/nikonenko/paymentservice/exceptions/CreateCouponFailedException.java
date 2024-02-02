package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CreateCouponFailedException extends StripeOperationFailedException {
    public CreateCouponFailedException(String message) {
        super(String.format("%s: %s", ErrorList.CREATE_COUPON_FAILED.getValue(), message));
    }
}
