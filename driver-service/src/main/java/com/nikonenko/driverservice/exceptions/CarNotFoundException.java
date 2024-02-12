package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException() {
        super(ExceptionList.CAR_NOT_FOUND.getValue());
    }
}