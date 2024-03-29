package com.nikonenko.driverservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogList {
    public static final String LOG_CREATE_CAR = "Car created with ID: {}";
    public static final String LOG_GET_CAR = "Got car with ID: {}";
    public static final String LOG_EDIT_CAR = "Car edited with ID: {}";
    public static final String LOG_DELETE_CAR = "Car deleted with ID: {}";
    public static final String LOG_CREATE_DRIVER = "Driver created with ID: {}";
    public static final String LOG_GET_DRIVER = "Got Driver with ID: {}";
    public static final String LOG_EDIT_DRIVER = "Driver edited with ID: {}";
    public static final String LOG_DELETE_DRIVER = "Driver deleted with ID: {}";
    public static final String LOG_ADD_RATING = "Added Review for Driver with ID: {}";
    public static final String LOG_ADD_CAR_FOR_DRIVER = "Added Car for Driver with ID: {}";
    public static final String LOG_DELETE_CAR_FOR_DRIVER = "Deleted Car for Driver with ID: {}";
    public static final String LOG_EXTRACT_DATA = "Extracted id: {}, username: {}, email: {}, phone: {}";
    public static final String LOG_EXTRACT_ROLES = "Extracted roles: {}";
    public static final String LOG_KAFKA_SEND_MESSAGE = "Sending message: {}";
    public static final String LOG_KAFKA_RECEIVE_CHANGE_RATING = "Receiver request for change rating Driver: {}";
    public static final String LOG_NOT_FOUND_ERROR = "Not Found exception thrown: {}";
    public static final String LOG_BAD_REQUEST_ERROR = "Bad Request exception thrown: {}";
    public static final String LOG_CONFLICT_ERROR = "Conflict exception thrown: {}";
    public static final String LOG_METHOD_ARGUMENT_ERROR = "Not Valid Method Argument exception thrown: {}";
    public static final String LOG_DECODE_ERROR = "Error decoding response body: {}";
    public static final String LOG_GET_RIDES_BY_DRIVER_ID_FEIGN_ERROR =
            "Exception during getRidesByDriverId request for Driver with ID {} to Ride Service: {}";
}
