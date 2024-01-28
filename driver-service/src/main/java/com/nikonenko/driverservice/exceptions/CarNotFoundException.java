package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException() {
        super(ErrorList.CAR_NOT_FOUND);
    }
}