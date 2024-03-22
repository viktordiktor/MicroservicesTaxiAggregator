package com.nikonenko.rideservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogList {
    public static final String LOG_CREATE_RIDE = "Created ride with id: {}";
    public static final String LOG_DELETE_RIDE = "Deleted ride with id: {}";
    public static final String LOG_ACCEPT_RIDE = "Accepted ride with id: {}";
    public static final String LOG_REJECT_RIDE = "Rejected ride with id: {}";
    public static final String LOG_START_RIDE = "Started ride with id: {}";
    public static final String LOG_FINISH_RIDE = "Finished ride with id: {}";
    public static final String LOG_SUCCESS_CHARGE = "Charge is success";
    public static final String LOG_EXTRACT_DATA = "Extracted id: {}, username: {}, email: {}, phone: {}";
    public static final String LOG_EXTRACT_ROLES = "Extracted roles: {}";
    public static final String LOG_KAFKA_SEND_MESSAGE = "Sending message {}";
    public static final String LOG_KAFKA_RECEIVE_CHANGE_PASSENGER_RATING = "Receiver request of Driver: Ride {}";
    public static final String LOG_KAFKA_RECEIVE_CHANGE_DRIVER_RATING = "Receiver request of Passenger: Ride {}";
    public static final String LOG_KAFKA_RECEIVE_CHANGE_RIDE_STATUS = "Receiver request for Ride {}";
    public static final String LOG_NOT_FOUND_ERROR = "Not Found exception thrown: {}";
    public static final String LOG_BAD_REQUEST_ERROR = "Bad Request exception thrown: {}";
    public static final String LOG_METHOD_ARGUMENT_ERROR = "Not Valid Method Argument exception thrown: {}";
    public static final String LOG_DECODE_ERROR = "Error decoding response body: {}";
    public static final String LOG_RETURN_CHARGE_FEIGN_ERROR =
            "Exception during returnCharge request for Charge with ID {} to Payment Service: {}";
    public static final String LOG_GET_CHARGE_BY_ID_FEIGN_ERROR =
            "Exception during getChargeById request for Charge with ID {} to Payment Service: {}";
}
