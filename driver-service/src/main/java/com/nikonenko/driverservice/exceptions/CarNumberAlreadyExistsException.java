package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class CarNumberAlreadyExistsException extends RuntimeException {
    public CarNumberAlreadyExistsException() {
        super(ErrorList.NUMBER_EXISTS);
    }
}
