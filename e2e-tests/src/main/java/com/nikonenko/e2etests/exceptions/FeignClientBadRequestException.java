package com.nikonenko.e2etests.exceptions;

public class FeignClientBadRequestException extends RuntimeException {
    public FeignClientBadRequestException(String message){
        super(message);
    }
}
