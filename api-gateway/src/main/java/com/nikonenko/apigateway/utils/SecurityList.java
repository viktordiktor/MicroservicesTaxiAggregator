package com.nikonenko.apigateway.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityList {
    public static final String ACTUATOR_PATH = "/actuator/**";
    public static final String EUREKA_PATH = "/eureka/**";
}
