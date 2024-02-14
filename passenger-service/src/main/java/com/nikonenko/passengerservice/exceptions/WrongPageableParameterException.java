package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ExceptionList.WRONG_PARAMETER.getValue());
    }
}
