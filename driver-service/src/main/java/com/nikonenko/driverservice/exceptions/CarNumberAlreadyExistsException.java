package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class CarNumberAlreadyExistsException extends RuntimeException {
    public CarNumberAlreadyExistsException() {
        super(ExceptionList.NUMBER_EXISTS.getValue());
    }
}
