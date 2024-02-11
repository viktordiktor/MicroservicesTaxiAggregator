package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class DriverNoRidesException extends RuntimeException {
    public DriverNoRidesException() {
        super(ErrorList.DRIVER_NO_RIDES);
    }
}
