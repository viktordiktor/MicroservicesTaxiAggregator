package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ErrorList.WRONG_PARAMETER);
    }
}
