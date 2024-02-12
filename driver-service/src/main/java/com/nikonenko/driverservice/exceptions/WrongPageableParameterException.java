package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ExceptionList.WRONG_PARAMETER.getValue());
    }
}
