package com.nikonenko.driverservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatternList {
    public static final String PHONE_PATTERN = "\\+375\\d{9}";
    public static final String NUMBER_PATTERN = "\\d{4}[a-zA-Z]{2}\\d";
}
