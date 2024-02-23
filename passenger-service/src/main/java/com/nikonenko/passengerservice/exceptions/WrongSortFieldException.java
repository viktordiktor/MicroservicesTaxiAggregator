package com.nikonenko.passengerservice.exceptions;

import com.nikonenko.passengerservice.utils.ExceptionList;

public class WrongSortFieldException extends RuntimeException {
    public WrongSortFieldException() {
        super(ExceptionList.WRONG_SORT_FIELD.getValue());
    }
}
