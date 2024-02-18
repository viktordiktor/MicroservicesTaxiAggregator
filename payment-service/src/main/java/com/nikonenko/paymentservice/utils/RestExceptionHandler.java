package com.nikonenko.paymentservice.utils;

import com.nikonenko.paymentservice.dto.ExceptionResponse;
import com.nikonenko.paymentservice.exceptions.CustomerAlreadyExistsException;
import com.nikonenko.paymentservice.exceptions.CustomerNotFoundException;
import com.nikonenko.paymentservice.exceptions.ExpiredCouponException;
import com.nikonenko.paymentservice.exceptions.InsufficientFundsException;
import com.nikonenko.paymentservice.exceptions.StripeOperationFailedException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessages.add(fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessages);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyExistsException(CustomerAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler({StripeOperationFailedException.class, HttpMessageNotReadableException.class,
            PropertyReferenceException.class, MethodArgumentTypeMismatchException.class,
            ExpiredCouponException.class})
    public ResponseEntity<ExceptionResponse> handleStripeOperationException(StripeOperationFailedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }
}
