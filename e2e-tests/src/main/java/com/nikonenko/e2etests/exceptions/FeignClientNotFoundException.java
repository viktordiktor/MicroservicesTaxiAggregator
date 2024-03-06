package com.nikonenko.e2etests.exceptions;

public class FeignClientNotFoundException extends RuntimeException {
    public FeignClientNotFoundException(String message){
        super(message);
    }
}
