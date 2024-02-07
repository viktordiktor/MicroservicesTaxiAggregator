package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class ExpiredCouponException extends RuntimeException {
    public ExpiredCouponException() {
        super(ExceptionList.EXPIRED_COUPON.getValue());
    }
}
