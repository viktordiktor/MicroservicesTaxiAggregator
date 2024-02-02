package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class RetrieveCouponFailedException extends StripeOperationFailedException {
    public RetrieveCouponFailedException(String message) {
        super(String.format("%s: %s", ErrorList.RETRIEVE_COUPON_FAILED.getValue(), message));
    }
}
