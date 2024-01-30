package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException() {
        super(ErrorList.DRIVER_NOT_FOUND);
    }
}
