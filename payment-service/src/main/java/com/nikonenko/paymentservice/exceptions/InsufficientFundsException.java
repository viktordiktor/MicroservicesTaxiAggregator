package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super(ErrorList.INSUFFICIENT_FUNDS.getValue());
    }
}
