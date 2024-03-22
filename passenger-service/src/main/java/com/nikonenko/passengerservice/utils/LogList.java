package com.nikonenko.passengerservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogList {
    public static final String LOG_CREATE_PASSENGER = "Passenger created with ID: {}";
    public static final String LOG_GET_PASSENGER = "Got Passenger with ID: {}";
    public static final String LOG_EDIT_PASSENGER = "Passenger edited with ID: {}";
    public static final String LOG_DELETE_PASSENGER = "Passenger deleted with ID: {}";
    public static final String LOG_ADD_RATING = "Added Review for Passenger with ID: {}";
    public static final String LOG_KAFKA_SEND_MESSAGE = "Sending message: {}";
    public static final String LOG_KAFKA_RECEIVE_CHANGE_RATING = "Receiver request for change rating Passenger: {}";
    public static final String LOG_GET_DISTANCE = "Got Ride Distance: {}";
    public static final String LOG_GET_PRICE = "Got Ride Price: {}";
    public static final String LOG_CUSTOMER_EXISTS = "Customer Exists!";
    public static final String LOG_GET_CHARGE = "Got Charge: {}";
    public static final String LOG_CREATE_RIDE = "Created ride with ID: {}";
    public static final String LOG_EXTRACT_DATA = "Extracted id: {}, username: {}, email: {}, phone: {}";
    public static final String LOG_EXTRACT_ROLES = "Extracted roles: {}";
    public static final String LOG_NOT_FOUND_ERROR = "Not Found exception thrown: {}";
    public static final String LOG_BAD_REQUEST_ERROR = "Bad Request exception thrown: {}";
    public static final String LOG_FORBIDDEN_ERROR = "Forbidden exception thrown: {}";
    public static final String LOG_CONFLICT_ERROR = "Conflict exception thrown: {}";
    public static final String LOG_METHOD_ARGUMENT_ERROR = "Not Valid Method Argument exception thrown: {}";
    public static final String LOG_DECODE_ERROR = "Error decoding response body: {}";
    public static final String LOG_CREATE_CHARGE_FEIGN_ERROR =
            "Exception during createCharge request for Passenger with ID {} to Payment Service: {}";
    public static final String LOG_CHECK_CUSTOMER_EXISTS_FEIGN_ERROR =
            "Exception during checkCustomerExists request for Passenger with ID {} to Payment Service: {}";
    public static final String LOG_CALCULATE_RIDE_PRICE_FEIGN_ERROR =
            "Exception during calculateRidePrice request to Payment Service: {}";
    public static final String LOG_CREATE_CUSTOMER_FEIGN_ERROR =
            "Exception during createCustomer request for Passenger with ID {} to Payment Service: {}";
    public static final String LOG_GET_RIDE_DISTANCE_FEIGN_ERROR =
            "Exception during getRideDistance request to Ride Service: {}";
    public static final String LOG_CREATE_RIDE_FEIGN_ERROR =
            "Exception during createRide request for Passenger with ID {} to Ride Service: {}";
    public static final String LOG_CLOSE_RIDE_FEIGN_ERROR =
            "Exception during closeRide request for Ride with ID {} to Ride Service: {}";
    public static final String LOG_GET_RIDES_BY_PASSENGER_ID_FEIGN_ERROR =
            "Exception during getRidesByPassengerId request for Passenger with ID {} to Ride Service: {}";
}
