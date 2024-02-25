package com.nikonenko.passengerservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationList {
    public static final String USERNAME_REQUIRED = "{username-required}";
    public static final String PHONE_REQUIRED = "{phone-required}";
    public static final String START_ADDRESS_REQUIRED = "{start-address-required}";
    public static final String END_ADDRESS_REQUIRED = "{end-address-required}";
    public static final String START_GEO_REQUIRED = "{start-geo-required}";
    public static final String END_GEO_REQUIRED = "{end-geo-required}";
    public static final String WRONG_MAX_USERNAME_SIZE = "{wrong-max-username-size}";
    public static final String WRONG_PHONE_FORMAT = "{wrong-phone-format}";
    public static final String WRONG_ENUM_FORMAT = "{wrong-enum-format}";
}
