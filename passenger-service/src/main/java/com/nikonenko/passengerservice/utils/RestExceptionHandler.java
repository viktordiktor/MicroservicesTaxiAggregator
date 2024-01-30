package com.nikonenko.passengerservice.utils;

import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.WrongPageableParameterException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<String> handlePassengerNotFoundException(PassengerNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
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

    @ExceptionHandler({UsernameAlreadyExistsException.class, PhoneAlreadyExistsException.class})
    public ResponseEntity<String> handleUsernameAlreadyExistsException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, PropertyReferenceException.class})
    public ResponseEntity<String> handleNotReadableException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(WrongPageableParameterException.class)
    public ResponseEntity<String> handleWrongPageableParameterException(WrongPageableParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}