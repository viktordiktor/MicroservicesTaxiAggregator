package com.nikonenko.passengerservice.utils;

import com.nikonenko.passengerservice.dto.ExceptionResponse;
import com.nikonenko.passengerservice.exceptions.AccessDeniedByPassengerException;
import com.nikonenko.passengerservice.exceptions.BadRequestByPassengerException;
import com.nikonenko.passengerservice.exceptions.KeycloakUserIsNotValid;
import com.nikonenko.passengerservice.exceptions.NotFoundByPassengerException;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.WrongPageableParameterException;
import com.nikonenko.passengerservice.exceptions.WrongSortFieldException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler({PassengerNotFoundException.class, NotFoundByPassengerException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundException(RuntimeException ex) {
        log.error(LogList.LOG_NOT_FOUND_ERROR, ex.getMessage());
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

        log.error(LogList.LOG_METHOD_ARGUMENT_ERROR, errorMessages);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessages);
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class, PhoneAlreadyExistsException.class})
    public ResponseEntity<ExceptionResponse> handleAlreadyExistsException(RuntimeException ex) {
        log.error(LogList.LOG_CONFLICT_ERROR, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, PropertyReferenceException.class,
            WrongPageableParameterException.class, MethodArgumentTypeMismatchException.class,
            BadRequestByPassengerException.class, WrongSortFieldException.class, KeycloakUserIsNotValid.class})
    public ResponseEntity<ExceptionResponse> handleBadRequestException(RuntimeException ex) {
        log.error(LogList.LOG_BAD_REQUEST_ERROR, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(AccessDeniedByPassengerException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedByPassengerException ex) {
        log.error(LogList.LOG_FORBIDDEN_ERROR, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}