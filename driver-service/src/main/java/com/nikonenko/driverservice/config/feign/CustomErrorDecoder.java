package com.nikonenko.driverservice.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikonenko.driverservice.dto.ExceptionResponse;
import com.nikonenko.driverservice.exceptions.BadRequestByDriverException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
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
                log.info("Received client exception with Status Code: {}", exceptionResponse.getHttpStatus());
                return new BadRequestByDriverException(exceptionResponse.getMessage());
            }
        } catch (IOException e) {
            log.error("Error decoding response body", e);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}