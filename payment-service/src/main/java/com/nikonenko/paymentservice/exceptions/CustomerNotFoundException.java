package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ExceptionList;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException() {
        super(ExceptionList.CUSTOMER_NOT_FOUND.getValue());
    }
}
