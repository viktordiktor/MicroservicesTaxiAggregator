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
    public final static String GENERATE_TOKEN_FAILED = "Exception during generating token: ";
    public final static String RETRIEVE_BALANCE_FAILED = "Exception during retrieving balance: ";
    public final static String RETRIEVE_CUSTOMER_FAILED = "Exception during retrieving customer: ";
    public final static String UPDATE_CUSTOMER_FAILED = "Exception during updating customer: ";
    public final static String NEGATIVE_AMOUNT = "Amount should be positive!";
    public final static String UNEXPECTED_MONTH_RANGE = "Month should be in range from 1 to 12!";
    public final static String UNEXPECTED_YEAR_RANGE = "Month should be in range from 2000 to 2100!";
}
