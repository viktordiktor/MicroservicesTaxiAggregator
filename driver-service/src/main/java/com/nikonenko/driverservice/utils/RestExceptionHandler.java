package com.nikonenko.driverservice.utils;

import com.nikonenko.driverservice.dto.ExceptionResponse;
import com.nikonenko.driverservice.exceptions.BadRequestByDriverException;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.CarNumberAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.DriverIsNotAvailableException;
import com.nikonenko.driverservice.exceptions.DriverNoRidesException;
import com.nikonenko.driverservice.exceptions.DriverNotAddedCarException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
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
    @ExceptionHandler({DriverNotFoundException.class, CarNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
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

    @ExceptionHandler({UsernameAlreadyExistsException.class, PhoneAlreadyExistsException.class,
            CarNumberAlreadyExistsException.class, HttpMessageNotReadableException.class,
            PropertyReferenceException.class, WrongPageableParameterException.class,
            MethodArgumentTypeMismatchException.class, DriverIsNotAvailableException.class,
            DriverNoRidesException.class, BadRequestByDriverException.class, DriverNotAddedCarException.class})
    public ResponseEntity<ExceptionResponse> handleAlreadyExistsException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }
}
