package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ErrorList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ErrorList.WRONG_PAGEABLE_PARAMETER.getValue());
    }
}
