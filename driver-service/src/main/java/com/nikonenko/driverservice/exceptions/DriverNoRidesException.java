package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class DriverNoRidesException extends RuntimeException {
    public DriverNoRidesException() {
        super(ExceptionList.DRIVER_NO_RIDES.getValue());
    }
}
