package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException() {
        super(ErrorList.NOT_FOUND);
    }
}
