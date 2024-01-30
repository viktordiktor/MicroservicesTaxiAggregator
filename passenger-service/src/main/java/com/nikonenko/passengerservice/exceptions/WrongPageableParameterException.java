package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ErrorList;

public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ErrorList.WRONG_PARAMETER);
    }
}
