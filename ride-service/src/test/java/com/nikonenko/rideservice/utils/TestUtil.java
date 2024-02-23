package com.nikonenko.rideservice.utils;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.drivers.CarResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RideAction;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TestUtil {
    public static final String DEFAULT_RIDE_ID = "ride1";
    public static final Long DEFAULT_PASSENGER_ID = 1L;
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public static final String DEFAULT_START_ADDRESS = "address1";
    public static final String DEFAULT_END_ADDRESS = "address2";
    public static final LocalDateTime DEFAULT_DATETIME = LocalDateTime.MIN;
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

    public ReviewRequest getReviewRequest() {
        return ReviewRequest.builder()
                .rideId(DEFAULT_RIDE_ID)
                .rating(DEFAULT_RATING)
                .comment(DEFAULT_COMMENT)
                .build();
    }
}
