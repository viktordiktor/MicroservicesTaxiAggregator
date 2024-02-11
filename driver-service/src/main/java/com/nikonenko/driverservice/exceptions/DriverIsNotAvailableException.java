package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class DriverIsNotAvailableException extends RuntimeException {
    public DriverIsNotAvailableException() {
        super(ErrorList.DRIVER_NOT_AVAILABLE);
    }
}
