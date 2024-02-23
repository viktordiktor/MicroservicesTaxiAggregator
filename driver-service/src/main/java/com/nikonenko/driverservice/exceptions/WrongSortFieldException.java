package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class WrongSortFieldException extends RuntimeException {
    public WrongSortFieldException() {
        super(ExceptionList.WRONG_SORT_FIELD.getValue());
    }
}
