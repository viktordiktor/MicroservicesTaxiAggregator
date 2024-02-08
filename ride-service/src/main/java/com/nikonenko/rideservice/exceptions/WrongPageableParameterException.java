package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ExceptionList.WRONG_PAGEABLE_PARAMETER.getValue());
    }
}
