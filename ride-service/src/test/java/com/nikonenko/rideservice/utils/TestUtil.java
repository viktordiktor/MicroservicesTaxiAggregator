package com.nikonenko.rideservice.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.ExceptionResponse;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.drivers.CarResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.exceptions.RideIsNotOpenedException;
import com.nikonenko.rideservice.exceptions.RideNotFoundException;
import com.nikonenko.rideservice.exceptions.WrongPageableParameterException;
import com.nikonenko.rideservice.exceptions.WrongSortFieldException;
import com.nikonenko.rideservice.integration.config.LocalDateTimeAdapter;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RideAction;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class TestUtil {
    public static final String DEFAULT_RIDE_ID = "ride1";
    public static final String NOT_EXISTING_RIDE_ID = "ride32193912";
    public static final UUID DEFAULT_PASSENGER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID DEFAULT_DRIVER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String DEFAULT_START_ADDRESS = "address1";
    public static final String DEFAULT_END_ADDRESS = "address2";
    public static final LocalDateTime DEFAULT_DATETIME = LocalDateTime.of(2024, 2, 27, 12, 12, 12);
    public static final String DEFAULT_CHARGE_ID = "charge1";
    public static final Double DEFAULT_DISTANCE = 10.0;
    public static final RideStatus OPENED_STATUS = RideStatus.OPENED;
    public static final RideStatus ACCEPTED_STATUS = RideStatus.ACCEPTED;
    public static final RideStatus STARTED_STATUS = RideStatus.STARTED;
    public static final RideStatus FINISHED_STATUS = RideStatus.FINISHED;
    public static final int DEFAULT_PAGE = 0;
    public static final int INVALID_PAGE = -1;
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int INVALID_PAGE_SIZE = 0;
    public static final String DEFAULT_PAGE_SORT = "id";
    public static final String INVALID_PAGE_SORT = "id))))";
    public static final LatLng DEFAULT_START_LATLNG = new LatLng(0.0, 0.0);
    public static final LatLng DEFAULT_END_LATLNG = new LatLng(0.0, 0.05);
    public static final CarResponse DEFAULT_CAR_RESPONSE = new CarResponse();
    public static final Double CALCULATED_DISTANCE = 5.565974539663679;
    public static final RidePaymentMethod PAYMENT_CARD = RidePaymentMethod.BY_CARD;
    public static final RidePaymentMethod PAYMENT_CASH = RidePaymentMethod.BY_CASH;
    public static final String DEFAULT_RETURN_ID = "return1";
    public static final String DEFAULT_CURRENCY = "usd";
    public static final BigDecimal DEFAULT_RETURN_AMOUNT = BigDecimal.ZERO;
    public static final RideAction RIDE_ACTION_ACCEPT = RideAction.ACCEPT;
    public static final RideAction RIDE_ACTION_FINISH = RideAction.FINISH;
    public final String DEFAULT_ID_PATH = "/api/v1/rides/{id}";
    public final String DEFAULT_PATH = "/api/v1/rides";
    public final String DEFAULT_OPEN_PATH = "/api/v1/rides/open";
    public final String DEFAULT_BY_PASSENGER_PATH = "/api/v1/rides/by-passenger/{passengerId}";
    public final String DEFAULT_BY_DRIVER_PATH = "/api/v1/rides/by-driver/{driverId}";
    public final String DEFAULT_DISTANCE_PATH = "/api/v1/rides/distance";
    public final String ID_PARAMETER = "id";
    public final String PASSENGER_ID_PARAMETER = "passengerId";
    public final String DRIVER_ID_PARAMETER = "driverId";
    public final String PAGE_NUMBER_PARAMETER = "pageNumber";
    public final String PAGE_SIZE_PARAMETER = "pageSize";
    public final String SORT_FIELD_PARAMETER = "sortField";
    public final String START_GEO_PARAMETER = "startGeo";
    public final String END_GEO_PARAMETER = "endGeo";
    public static final int DEFAULT_RATING = 5;
    public static final String DEFAULT_COMMENT = "comment1";
    public static final boolean SUCCESS_TRUE = true;
    public static final boolean SUCCESS_FALSE = false;

    public RideResponse getRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .status(OPENED_STATUS)
                .paymentMethod(RidePaymentMethod.BY_CARD)
                .build();
    }

    public RideResponse getRideResponseWithParameters(UUID passengerId, String startAddress,
                                                          String endAddress, String chargeId) {
        RideResponse rideResponse = getRideResponse();
        rideResponse.setPassengerId(passengerId);
        rideResponse.setStartAddress(startAddress);
        rideResponse.setEndAddress(endAddress);
        rideResponse.setChargeId(chargeId);
        return rideResponse;
    }

    public RideResponse getCreateRideResponseByCash() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .distance(DEFAULT_DISTANCE)
                .status(OPENED_STATUS)
                .paymentMethod(RidePaymentMethod.BY_CASH)
                .build();
    }

    public List<Ride> getRideList() {
        return List.of(getOpenedByCardRide(), getAcceptedRide(), getStartedRide(), getFinishedRide());
    }

    public List<Ride> getOpenedRideList() {
        return List.of(getOpenedByCardRide(), getAcceptedRide(), getStartedRide(), getFinishedRide());
    }

    public List<RideResponse> getRideResponseList(List<Ride> rideList, ModelMapper modelMapper) {
        return rideList.stream()
                .map(ride -> modelMapper.map(ride, RideResponse.class))
                .collect(Collectors.toList());
    }

    public Ride getNotSavedRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .distance(DEFAULT_DISTANCE)
                .status(OPENED_STATUS)
                .build();
    }

    public Ride getNotSavedRideWithParameters(UUID passengerId, String startAddress,
                                              String endAddress, String chargeId) {
        Ride ride = getNotSavedRide();
        ride.setPassengerId(passengerId);
        ride.setStartAddress(startAddress);
        ride.setEndAddress(endAddress);
        ride.setChargeId(chargeId);
        return ride;
    }

    public Ride getOpenedByCardRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .status(OPENED_STATUS)
                .paymentMethod(PAYMENT_CARD)
                .build();
    }

    public Ride getOpenedByCashRideWithRideId(String rideId) {
        Ride ride = getOpenedByCashRide();
        ride.setId(rideId);
        return ride;
    }

    public Ride getOpenedByCardRideWithRideId(String rideId) {
        Ride ride = getOpenedByCardRide();
        ride.setId(rideId);
        return ride;
    }

    public Ride getOpenedRideWithParameters(UUID passengerId, String startAddress,
                                                  String endAddress, String chargeId) {
        Ride ride = getOpenedByCardRide();
        ride.setPassengerId(passengerId);
        ride.setStartAddress(startAddress);
        ride.setEndAddress(endAddress);
        ride.setChargeId(chargeId);
        return ride;
    }

    public Ride getOpenedByCashRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .distance(DEFAULT_DISTANCE)
                .status(OPENED_STATUS)
                .paymentMethod(PAYMENT_CASH)
                .build();
    }

    public Ride getAcceptedRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(new CarResponse())
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .status(ACCEPTED_STATUS)
                .build();
    }

    public Ride getStartedRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .status(STARTED_STATUS)
                .build();
    }

    public Ride getFinishedRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .car(DEFAULT_CAR_RESPONSE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATETIME)
                .endDate(DEFAULT_DATETIME)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .status(FINISHED_STATUS)
                .build();
    }

    public Ride getFinishedRideWithRideId(String rideId) {
        Ride finishedRide = getFinishedRide();
        finishedRide.setId(rideId);
        return finishedRide;
    }

    public CalculateDistanceResponse getCalculateDistanceResponse() {
        return CalculateDistanceResponse.builder()
                .startGeo(DEFAULT_START_LATLNG)
                .endGeo(DEFAULT_END_LATLNG)
                .distance(CALCULATED_DISTANCE)
                .build();
    }

    public CreateRideRequest getCreateRideRequestByCard() {
        return CreateRideRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .distance(DEFAULT_DISTANCE)
                .chargeId(DEFAULT_CHARGE_ID)
                .build();
    }

    public CreateRideRequest getCreateRideRequestWithParameters(UUID passengerId, String startAddress,
                                                                      String endAddress, String chargeId) {
        return CreateRideRequest.builder()
                .passengerId(passengerId)
                .startAddress(startAddress)
                .endAddress(endAddress)
                .distance(DEFAULT_DISTANCE)
                .chargeId(chargeId)
                .build();
    }

    public CreateRideRequest getCreateRideRequestByCash() {
        return CreateRideRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .distance(DEFAULT_DISTANCE)
                .build();
    }

    public CustomerChargeResponse getSuccessfulCustomerChargeResponse() {
        return CustomerChargeResponse.builder()
                .success(SUCCESS_TRUE)
                .build();
    }

    public CustomerChargeResponse getUnsuccessfulCustomerChargeResponse() {
        return CustomerChargeResponse.builder()
                .success(SUCCESS_FALSE)
                .build();
    }

    public CloseRideResponse getCloseRideByCardResponse() {
        return CloseRideResponse.builder()
                .customerChargeReturnResponse(getCustomerChargeReturnResponse())
                .ridePaymentMethod(PAYMENT_CARD)
                .build();
    }

    public CloseRideResponse getCloseRideByCashResponse() {
        return CloseRideResponse.builder()
                .ridePaymentMethod(PAYMENT_CASH)
                .build();
    }

    public CustomerChargeReturnResponse getCustomerChargeReturnResponse() {
        return CustomerChargeReturnResponse.builder()
                .id(DEFAULT_RETURN_ID)
                .paymentId(DEFAULT_CHARGE_ID)
                .currency(DEFAULT_CURRENCY)
                .amount(DEFAULT_RETURN_AMOUNT)
                .build();
    }

    public ChangeRideStatusRequest getAcceptChangeRideStatusRequest() {
        return ChangeRideStatusRequest.builder()
                .rideId(DEFAULT_RIDE_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .rideAction(RIDE_ACTION_ACCEPT)
                .car(DEFAULT_CAR_RESPONSE)
                .build();
    }

    public ChangeRideStatusRequest getFinishChangeRideStatusRequest() {
        return ChangeRideStatusRequest.builder()
                .rideId(DEFAULT_RIDE_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .rideAction(RIDE_ACTION_FINISH)
                .car(DEFAULT_CAR_RESPONSE)
                .build();
    }

    public ChangeRideStatusRequest getChangeRideStatusRequestWithParameters(String rideId, String rideAction) {
        return ChangeRideStatusRequest.builder()
                .rideId(rideId)
                .driverId(DEFAULT_DRIVER_ID)
                .rideAction(RideAction.valueOf(rideAction))
                .car(DEFAULT_CAR_RESPONSE)
                .build();
    }

    public ReviewRequest getReviewRequest() {
        return ReviewRequest.builder()
                .rideId(DEFAULT_RIDE_ID)
                .rating(DEFAULT_RATING)
                .comment(DEFAULT_COMMENT)
                .build();
    }

    public void assertEqualsJsonPageResponse(List<RideResponse> responses, PageResponse<RideResponse> result) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    public void assertEqualsAllRideResponseFieldsWithoutId(RideResponse response, RideResponse result) {
        assertEquals(response.getDistance(), result.getDistance());
        assertEquals(response.getDriverId(), result.getDriverId());
        assertEquals(response.getCar(), result.getCar());
        assertEquals(response.getStartAddress(), result.getStartAddress());
        assertEquals(response.getEndAddress(), result.getEndAddress());
        assertEquals(response.getStartDate(), result.getStartDate());
        assertEquals(response.getEndDate(), result.getEndDate());
        assertEquals(response.getStatus(), result.getStatus());
        assertEquals(response.getChargeId(), result.getChargeId());
        assertEquals(response.getPaymentMethod(), result.getPaymentMethod());
    }

    public ExceptionResponse getNotFoundExceptionResponse() {
        return getBasicExceptionResponse(new RideNotFoundException(), HttpStatus.NOT_FOUND);
    }

    public ExceptionResponse getWrongPageableParameterExceptionResponse() {
        return getBasicExceptionResponse(new WrongPageableParameterException(), HttpStatus.BAD_REQUEST);
    }

    public ExceptionResponse getWrongSortFieldExceptionResponse() {
        return getBasicExceptionResponse(new WrongSortFieldException(), HttpStatus.BAD_REQUEST);
    }

    public ExceptionResponse getRideIsNotOpenedExceptionResponse() {
        return getBasicExceptionResponse(new RideIsNotOpenedException(), HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse getBasicExceptionResponse(Exception ex, HttpStatus status) {
        return ExceptionResponse.builder()
                .message(ex.getMessage())
                .httpStatus(status)
                .build();
    }
}
