package com.nikonenko.passengerservice.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class RequestInterceptUtil {
    public static String getAuthorizationHeader() {
        HttpServletRequest originalRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return originalRequest.getHeader(HttpHeaders.AUTHORIZATION);
    }
}
