package com.nikonenko.paymentservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogList {
    public static final String LOG_KAFKA_SEND_MESSAGE = "Sending message: {}";
    public static final String LOG_CREATE_TOKEN = "Created Token with ID: {}";
    public static final String LOG_CREATE_CHARGE = "Created Charge with ID: {}";
    public static final String LOG_RETRIEVE_CHARGE = "Retrieved Charge with ID: {}";
    public static final String LOG_CREATE_COUPON = "Created Coupon with ID: {}";
    public static final String LOG_RETRIEVE_COUPON = "Retrieved Coupon with ID: {}";
    public static final String LOG_CREATE_CUSTOMER = "Created Customer with ID: {}";
    public static final String LOG_RETRIEVE_CUSTOMER = "Retrieved Customer with ID: {}";
    public static final String LOG_CREATE_PAYMENT_METHOD = "Created Payment Method with ID: {}";
    public static final String LOG_CONFIRM_INTENT = "Confirmed Intent with ID: {}";
    public static final String LOG_CREATE_INTENT = "Created Intent with ID: {}";
    public static final String LOG_RETRIEVE_INTENT = "Retrieved Intent with ID: {}";
    public static final String LOG_UPDATE_CUSTOMER = "Updated Customer with ID: {}";
    public static final String LOG_COUPON_EXP_DATE = "Coupon expiration date: {}\nRequest date: {}";
    public static final String LOG_EXTRACT_DATA = "Extracted id: {}, username: {}, email: {}, phone: {}";
    public static final String LOG_EXTRACT_ROLES = "Extracted roles: {}";
    public static final String LOG_NOT_FOUND_ERROR = "Not Found exception thrown: {}";
    public static final String LOG_BAD_STRIPE_REQUEST_ERROR = "Bad Request during Stripe operation exception thrown: {}";
    public static final String LOG_CONFLICT_ERROR = "Conflict exception thrown: {}";
    public static final String LOG_PAYMENT_REQUIRED_ERROR = "Payment Required exception thrown: {}";
    public static final String LOG_METHOD_ARGUMENT_ERROR = "Not Valid Method Argument exception thrown: {}";
}
