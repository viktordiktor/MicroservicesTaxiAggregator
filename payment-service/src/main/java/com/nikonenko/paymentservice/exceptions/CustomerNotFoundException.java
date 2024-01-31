package com.nikonenko.paymentservice.exceptions;

import com.nikonenko.paymentservice.utils.ErrorList;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException() {
        super(ErrorList.CUSTOMER_NOT_FOUND);
    }
}
