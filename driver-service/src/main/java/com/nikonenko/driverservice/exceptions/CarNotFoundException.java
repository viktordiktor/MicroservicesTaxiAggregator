package com.nikonenko.driverservice.exceptions;

import com.nikonenko.driverservice.utils.ErrorList;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException() {
        super(ErrorList.CAR_NOT_FOUND);
    }
}