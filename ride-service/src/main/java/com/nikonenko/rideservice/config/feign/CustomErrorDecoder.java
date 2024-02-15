package com.nikonenko.rideservice.config.feign;

import com.nikonenko.rideservice.exceptions.BadRequestByRideException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        FeignException exception = FeignException.errorStatus(s, response);
        int status = response.status();
        if (status >= 500) {
            return new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    (Long) null,
                    response.request());
        }
        if (status == 400) {
            return new BadRequestByRideException(exception.getMessage());
        }
        return exception;
    }
}