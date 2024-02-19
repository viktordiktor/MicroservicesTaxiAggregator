package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class DriverNotAddedCarException extends RuntimeException {
    public DriverNotAddedCarException() {
        super(ExceptionList.DRIVER_NO_CAR.getValue());
    }
}
