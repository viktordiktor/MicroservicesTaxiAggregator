package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ErrorList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPageableParameterException extends RuntimeException {
    public WrongPageableParameterException() {
        super(ErrorList.WRONG_PARAMETER);
    }
}
