package com.nikonenko.paymentservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorList {
    public final static String CUSTOMER_ALREADY_EXISTS = "This customer is already exists!";
    public final static String INSUFFICIENT_FUNDS = "Insufficient funds!";
    public final static String CUSTOMER_NOT_FOUND = "Customer not found!";
    public final static String CONFIRM_INTENT_FAILED = "Exception during confirming intent: ";
    public final static String CREATE_CHARGE_FAILED = "Exception during creating charge: ";
    public final static String CREATE_CUSTOMER_FAILED = "Exception during creating customer: ";
    public final static String CREATE_INTENT_FAILED = "Exception during creating intent: ";
    public final static String CREATE_PAYMENT_FAILED = "Exception during creating payment: ";
    public final static String CREATE_TOKEN_FAILED = "Exception during creating token: ";
    public final static String CREATE_COUPON_FAILED = "Exception during creating coupon: ";
    public final static String RETRIEVE_BALANCE_FAILED = "Exception during retrieving balance: ";
    public final static String RETRIEVE_CUSTOMER_FAILED = "Exception during retrieving customer: ";
    public final static String RETRIEVE_COUPON_FAILED = "Exception during retrieving coupon: ";
    public final static String UPDATE_CUSTOMER_FAILED = "Exception during updating customer: ";
    public final static String NEGATIVE_VALUE = "Value should be positive!";
    public final static String UNEXPECTED_MONTH_RANGE = "Month should be in range from 1 to 12!";
    public final static String UNEXPECTED_YEAR_RANGE = "Year should be in range from 00 to 99!";
    public static final String WRONG_CARD_FORMAT = "Card must be a 16-digit number!";
    public static final String WRONG_PHONE_FORMAT = "Phone must be in the format +375*********";
    public static final String WRONG_CVC_FORMAT = "CVC must be a 3-digit number!";
    public static final String WRONG_DATETIME_FORMAT = "DateTime format should be yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String EXPIRED_COUPON = "This coupon is expired!";
    public static final String LENGTH_REQUIRED = "Ride length is required!";
    public static final String DATETIME_REQUIRED = "Ride date time is required!";
    public static final String PASSENGER_ID_REQUIRED = "Passenger ID is required!";
    public static final String CURRENCY_REQUIRED = "Currency is required!";
    public static final String AMOUNT_REQUIRED = "Amount is required!";
    public static final String USERNAME_REQUIRED = "Username is required!";
    public static final String PHONE_REQUIRED = "Phone is required!";
    public static final String CARD_REQUIRED = "Card is required!";
    public static final String EXP_MONTH_REQUIRED = "Expiration month is required!";
    public static final String EXP_YEAR_REQUIRED = "Expiration year is required!";
    public static final String CVC_REQUIRED = "CVC is required!";
    public static final String TOKEN_REQUIRED = "Token is required!";
    public static final String DURATION_REQUIRED = "Coupon duration is required!";
    public static final String PERCENT_REQUIRED = "Coupon percent off is required!";
}
