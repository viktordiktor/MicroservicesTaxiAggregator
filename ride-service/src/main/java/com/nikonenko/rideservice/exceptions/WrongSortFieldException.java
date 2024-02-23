package com.nikonenko.rideservice.exceptions;

import com.nikonenko.rideservice.utils.ExceptionList;

public class WrongSortFieldException extends RuntimeException {
    public WrongSortFieldException() {
        super(ExceptionList.WRONG_SORT_FIELD.getValue());
    }
}
