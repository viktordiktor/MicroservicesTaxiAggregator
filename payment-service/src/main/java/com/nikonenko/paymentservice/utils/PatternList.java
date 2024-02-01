package com.nikonenko.paymentservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatternList {
    public static final String PHONE_PATTERN = "^\\+375\\d{9}$";
    public static final String CARD_PATTERN = "^\\d{16}$";
}
