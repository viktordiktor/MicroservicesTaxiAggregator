package com.nikonenko.passengerservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorList {
    public static final String USERNAME_EXISTS = "Passenger with this username already exists!";
    public static final String PHONE_EXISTS = "Passenger with this phone already exists!";
    public static final String NOT_FOUND = "Passenger not found!";
    public static final String WRONG_PARAMETER = "Wrong pageable parameter!";
    public static final String MIN_RATING_WRONG = "Rating must be at least 1";
    public static final String MAX_RATING_WRONG = "Rating must be at most 5";
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String PHONE_REQUIRED = "Phone is required";
    public static final String WRONG_MAX_USERNAME_SIZE = "Username must not exceed 20 characters";
    public static final String WRONG_PHONE_FORMAT = "Phone must be in the format +375*********";
}
