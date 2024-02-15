package com.nikonenko.rideservice.config.feign;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    public static final long RETRY_PERIOD = 100L;
    public static final long RETRY_MAX_PERIOD = 5000L;
    public static final int RETRY_MAX_ATTEMPTS = 10;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(RETRY_PERIOD, RETRY_MAX_PERIOD, RETRY_MAX_ATTEMPTS);
    }
}
