package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ExceptionList;

public class BadRequestByDriverException extends RuntimeException {
    public BadRequestByDriverException(String message) {
        super(String.format("%s: %s", ExceptionList.BAD_REQUEST_BY_DRIVER.getValue(), message));
    }
}
