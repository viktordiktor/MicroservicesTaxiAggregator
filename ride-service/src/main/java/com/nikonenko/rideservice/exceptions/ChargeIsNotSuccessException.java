package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class ChargeIsNotSuccessException extends RuntimeException {
    public ChargeIsNotSuccessException() {
        super(ErrorList.CHARGE_IS_NOT_SUCCESS.getValue());
    }
}
