package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super(ExceptionList.INSUFFICIENT_FUNDS.getValue());
    }
}
