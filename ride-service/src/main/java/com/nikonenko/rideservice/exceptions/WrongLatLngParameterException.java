package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class WrongLatLngParameterException extends RuntimeException {
    public WrongLatLngParameterException() {
        super(ExceptionList.WRONG_LAT_LNG_PARAMETER.getValue());
    }
}
