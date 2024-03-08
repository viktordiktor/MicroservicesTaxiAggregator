package com.nikonenko.e2etests.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikonenko.e2etests.dto.ExceptionResponse;
import com.nikonenko.e2etests.exceptions.FeignClientBadRequestException;
import com.nikonenko.e2etests.utils.LogList;
import feign.Response;
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
            if (exceptionResponse.getHttpStatus().is4xxClientError()) {
                return new FeignClientBadRequestException(exceptionResponse.getMessage());
            }
        } catch (IOException ex) {
            log.error(LogList.LOG_DECODE_ERROR, ex.getMessage());
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
