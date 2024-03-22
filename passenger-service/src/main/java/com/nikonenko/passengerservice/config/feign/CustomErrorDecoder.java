package com.nikonenko.passengerservice.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikonenko.passengerservice.dto.ExceptionResponse;
import com.nikonenko.passengerservice.exceptions.AccessDeniedByPassengerException;
import com.nikonenko.passengerservice.exceptions.BadRequestByPassengerException;
import com.nikonenko.passengerservice.utils.ExceptionList;
import com.nikonenko.passengerservice.utils.LogList;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultErrorDecoder = new Default();

    public CustomErrorDecoder() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            ExceptionResponse exceptionResponse = objectMapper.readValue(bodyInputStream, ExceptionResponse.class);
            if (exceptionResponse.getHttpStatus().is5xxServerError()) {
                return new RetryableException(
                        response.status(),
                        exceptionResponse.getMessage(),
                        response.request().httpMethod(),
                        FeignException.errorStatus(methodKey, response),
                        (Long) null,
                        response.request());
            }
            if (exceptionResponse.getHttpStatus().is4xxClientError()) {
                return exceptionResponse.getHttpStatus() != HttpStatus.FORBIDDEN ?
                        new BadRequestByPassengerException(exceptionResponse.getMessage()) :
                        new AccessDeniedByPassengerException(ExceptionList.ACCESS_DENIED_BY_PASSENGER.getValue());
            }
        } catch (IOException ex) {
            log.error(LogList.LOG_DECODE_ERROR, ex.getMessage());
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
