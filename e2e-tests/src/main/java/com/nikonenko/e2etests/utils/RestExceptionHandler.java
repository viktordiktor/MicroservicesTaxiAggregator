package com.nikonenko.e2etests.utils;

import com.nikonenko.e2etests.dto.ExceptionResponse;
import com.nikonenko.e2etests.exceptions.FeignClientBadRequestException;
import com.nikonenko.e2etests.exceptions.FeignClientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = {FeignClientBadRequestException.class})
    public ResponseEntity<ExceptionResponse> handleFeignClientBadRequestException
                                                    (FeignClientBadRequestException feignClientBadRequestException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(feignClientBadRequestException.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(value = {FeignClientNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleFeignClientNotFoundException
                                                        (FeignClientNotFoundException feignClientNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(feignClientNotFoundException.getMessage(), HttpStatus.NOT_FOUND));
    }
}