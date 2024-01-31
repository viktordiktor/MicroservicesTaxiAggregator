package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class ExpiredCouponException extends RuntimeException {
    public ExpiredCouponException() {
        super(ErrorList.EXPIRED_COUPON);
    }
}
