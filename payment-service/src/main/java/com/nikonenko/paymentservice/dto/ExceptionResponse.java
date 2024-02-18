package com.nikonenko.paymentservice.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
public class ExceptionResponse {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public ExceptionResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }
}
