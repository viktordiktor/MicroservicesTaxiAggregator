package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class ChargeIsNotSuccessException extends RuntimeException {
    public ChargeIsNotSuccessException() {
        super(ExceptionList.CHARGE_IS_NOT_SUCCESS.getValue());
    }
}
