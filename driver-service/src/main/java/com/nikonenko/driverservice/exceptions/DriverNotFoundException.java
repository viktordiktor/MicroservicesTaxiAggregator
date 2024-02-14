package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException() {
        super(ExceptionList.DRIVER_NOT_FOUND.getValue());
    }
}
