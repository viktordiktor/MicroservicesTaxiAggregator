package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class DriverIsNotAvailableException extends RuntimeException {
    public DriverIsNotAvailableException() {
        super(ExceptionList.DRIVER_NOT_AVAILABLE.getValue());
    }
}
