package com.nikonenko.passengerservice.utils;

import com.google.maps.model.LatLng;
import com.nikonenko.passengerservice.dto.CustomerCreationRequest;
import com.nikonenko.passengerservice.dto.CustomerDataRequest;
import com.nikonenko.passengerservice.dto.ExceptionResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingPassengerResponse;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CarResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.WrongPageableParameterException;
import com.nikonenko.passengerservice.exceptions.WrongSortFieldException;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.models.RatingPassenger;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.models.feign.RideStatus;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class TestUtil {
    public final int DEFAULT_PAGE = 0;
    public final int INVALID_PAGE = -1;
    public final int DEFAULT_PAGE_SIZE = 5;
    public final int INVALID_PAGE_SIZE = 0;
    public final String DEFAULT_PAGE_SORT = "id";
    public final String INVALID_PAGE_SORT = "aaa";
    public final UUID DEFAULT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public final Long DEFAULT_REVIEW_ID = 1L;
    public final UUID SECOND_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public final Long SECOND_REVIEW_ID = 2L;
    public final UUID NOT_EXISTING_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    public final String DEFAULT_USERNAME = "USERNAME1";
    public final String SECOND_USERNAME = "USERNAME2";
    public final String DEFAULT_PHONE = "+375484848484";
    public final String SECOND_PHONE = "+375222222222";
    public final UUID UPDATING_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public final UUID CREATION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public final String CREATION_PHONE = "+375191911919";
    public final String EXISTING_PHONE = "+375111111111";
    public final String CREATION_USERNAME = "viktordiktor";
    public final String EXISTING_USERNAME = "JohnDoe";
    public final Integer DEFAULT_RATING = 5;
    public final String DEFAULT_RATING_COMMENT = "Comment1";
    public final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    public final String DEFAULT_START_ADDRESS = "address1";
    public final String DEFAULT_END_ADDRESS = "address2";
    public final LatLng DEFAULT_START_GEO = new LatLng();
    public final LatLng DEFAULT_END_GEO = new LatLng();
    public final RidePaymentMethod PAYMENT_CARD = RidePaymentMethod.BY_CARD;
    public final RidePaymentMethod PAYMENT_CASH = RidePaymentMethod.BY_CASH;
    public final Double DEFAULT_DISTANCE = 1.0;
    public final String DEFAULT_CHARGE_ID = "charge1";
    public final String DEFAULT_CURRENCY = "usd";
    public final boolean DEFAULT_SUCCESS = true;
    public final String DEFAULT_RIDE_ID = "ride1";
    public final LocalDateTime DEFAULT_DATE = LocalDateTime.now();
    public final RideStatus DEFAULT_RIDE_STATUS = RideStatus.OPENED;
    public final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    public final String DEFAULT_ID_PATH = "/api/v1/passengers/{id}";
    public final String DEFAULT_PATH = "/api/v1/passengers";
    public final String ID_PARAMETER = "id";
    public final String PAGE_NUMBER_PARAMETER = "pageNumber";
    public final String PAGE_SIZE_PARAMETER = "pageSize";
    public final String SORT_FIELD_PARAMETER = "sortField";
    public final String PASSENGER_ROLE = "ROLE_PASSENGER";

    public List<Passenger> getPassengerList() {
        return List.of(getDefaultPassenger(), getSecondPassenger());
    }

    public Passenger getDefaultPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .phone(DEFAULT_PHONE)
                .username(DEFAULT_USERNAME)
                .ratingSet(getDefaultRatingSet())
                .build();
    }

    public OAuth2User getDefaultOAuth2User() {
        Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority(PASSENGER_ROLE));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(SecurityUtil.PHONE, DEFAULT_PHONE);
        attributes.put(SecurityUtil.USERNAME, DEFAULT_USERNAME);

        return new DefaultOAuth2User(authorities, attributes, SecurityUtil.USERNAME);
    }

    public OAuth2User getOAuth2UserWithParameters(String username, String phone) {
        Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority(PASSENGER_ROLE));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(SecurityUtil.PHONE, phone);
        attributes.put(SecurityUtil.USERNAME, username);

        return new DefaultOAuth2User(authorities, attributes, SecurityUtil.USERNAME);
    }

    public Passenger getPassengerWithParameters(String id, String username, String phone) {
        return Passenger.builder()
                .id(UUID.fromString(id))
                .phone(username)
                .username(phone)
                .build();
    }

    public Passenger getSecondPassenger() {
        return Passenger.builder()
                .id(SECOND_ID)
                .phone(SECOND_PHONE)
                .username(SECOND_USERNAME)
                .ratingSet(getDefaultRatingSet())
                .build();
    }

    public Passenger getNotSavedPassenger() {
        return Passenger.builder()
                .phone(DEFAULT_PHONE)
                .username(DEFAULT_USERNAME)
                .ratingSet(getDefaultRatingSet())
                .build();
    }

    public Passenger getNotSavedCreationPassenger() {
        return Passenger.builder()
                .phone(CREATION_PHONE)
                .username(CREATION_USERNAME)
                .build();
    }

    public Passenger getSavedCreationPassenger() {
        return Passenger.builder()
                .id(CREATION_ID)
                .phone(CREATION_PHONE)
                .username(CREATION_USERNAME)
                .build();
    }

    public PassengerRequest getDefaultPassengerRequest() {
        return PassengerRequest.builder()
                .phone(DEFAULT_PHONE)
                .username(DEFAULT_USERNAME)
                .build();
    }

    public PassengerRequest getPassengerRequestWithParameters(String username, String phone) {
        return PassengerRequest.builder()
                .phone(phone)
                .username(username)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_ID)
                .phone(DEFAULT_PHONE)
                .username(DEFAULT_USERNAME)
                .ratingSet(getDefaultRatingSetResponse())
                .build();
    }

    public PassengerRequest getUpdatePassengerRequest() {
        return PassengerRequest.builder()
                .username(SECOND_USERNAME)
                .phone(SECOND_PHONE)
                .build();
    }

    public PassengerResponse getUpdatePassengerResponse() {
        return PassengerResponse.builder()
                .id(UPDATING_ID)
                .username(SECOND_USERNAME)
                .phone(SECOND_PHONE)
                .ratingSet(new HashSet<>())
                .build();
    }

    public PassengerResponse getCreationPassengerResponse() {
        return PassengerResponse.builder()
                .id(CREATION_ID)
                .username(CREATION_USERNAME)
                .phone(CREATION_PHONE)
                .build();
    }

    public PassengerRequest getCreationPassengerRequest() {
        return PassengerRequest.builder()
                .username(CREATION_USERNAME)
                .phone(CREATION_PHONE)
                .build();
    }

    public PassengerRequest getPassengerRequestWithExistingPhoneRequest() {
        return PassengerRequest.builder()
                .username(CREATION_USERNAME)
                .phone(EXISTING_PHONE)
                .build();
    }

    public PassengerRequest getPassengerRequestWithExistingUsernameRequest() {
        return PassengerRequest.builder()
                .username(EXISTING_USERNAME)
                .phone(CREATION_PHONE)
                .build();
    }

    public List<PassengerResponse> getPassengerResponseList(ModelMapper modelMapper, List<Passenger> passengers) {
        return passengers.stream()
                .map(passenger -> modelMapper.map(passenger, PassengerResponse.class))
                .toList();
    }

    public Set<RatingPassenger> getDefaultRatingSet() {
        return Set.of(new RatingPassenger(DEFAULT_REVIEW_ID, DEFAULT_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingPassenger(SECOND_REVIEW_ID, SECOND_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public Set<RatingPassengerResponse> getDefaultRatingSetResponse() {
        return Set.of(new RatingPassengerResponse(DEFAULT_REVIEW_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingPassengerResponse(SECOND_REVIEW_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public RatingToPassengerRequest getRatingToPassengerRequest() {
        return RatingToPassengerRequest.builder()
                .passengerId(DEFAULT_ID)
                .rating(DEFAULT_RATING)
                .comment(DEFAULT_RATING_COMMENT)
                .build();
    }

    public RatingToPassengerRequest getRatingToPassengerRequestWithParameters(String id, int rating, String comment) {
        return RatingToPassengerRequest.builder()
                .passengerId(UUID.fromString(id))
                .rating(rating)
                .comment(comment)
                .build();
    }

    public CustomerDataRequest getCustomerDataRequest() {
        return CustomerDataRequest.builder()
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public CustomerDataRequest getCustomerDataRequestWithParameters(String username, String phone, String amount) {
        return CustomerDataRequest.builder()
                .username(username)
                .phone(phone)
                .amount(new BigDecimal(amount))
                .build();
    }

    public CustomerCreationRequest getCustomerCreationRequest() {
        return CustomerCreationRequest.builder()
                .passengerId(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public CustomerCreationRequest getCustomerCreationRequestWithParameters
                                                            (String id, String username, String phone, String amount) {
        return CustomerCreationRequest.builder()
                .passengerId(UUID.fromString(id))
                .username(username)
                .phone(phone)
                .amount(new BigDecimal(amount))
                .build();
    }

    public RideByPassengerRequest getRideByPassengerCardRequest() {
        return RideByPassengerRequest.builder()
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startGeo(DEFAULT_START_GEO)
                .endGeo(DEFAULT_END_GEO)
                .ridePaymentMethod(PAYMENT_CARD)
                .build();
    }

    public RideByPassengerRequest getRideByPassengerCashRequest() {
        return RideByPassengerRequest.builder()
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startGeo(DEFAULT_START_GEO)
                .endGeo(DEFAULT_END_GEO)
                .ridePaymentMethod(PAYMENT_CASH)
                .build();
    }

    public CalculateDistanceRequest getCalculateDistanceRequest() {
        return CalculateDistanceRequest.builder()
                .startGeo(DEFAULT_START_GEO)
                .endGeo(DEFAULT_END_GEO)
                .build();
    }

    public CalculateDistanceResponse getCalculateDistanceResponse() {
        return CalculateDistanceResponse.builder()
                .startGeo(DEFAULT_START_GEO)
                .endGeo(DEFAULT_END_GEO)
                .distance(DEFAULT_DISTANCE)
                .build();
    }

    public CustomerExistsResponse getCustomerExistsResponse() {
        return CustomerExistsResponse.builder()
                .isExists(true)
                .build();
    }

    public CustomerExistsResponse getNotExistingCustomerExistsResponse() {
        return CustomerExistsResponse.builder()
                .isExists(false)
                .build();
    }

    public CustomerCalculateRideResponse getCustomerCalculateRideResponse() {
        return CustomerCalculateRideResponse.builder()
                .rideDateTime(DEFAULT_DATE)
                .rideLength(DEFAULT_DISTANCE)
                .price(DEFAULT_PRICE)
                .build();
    }

    public CustomerChargeRequest getCustomerChargeRequest() {
        return CustomerChargeRequest.builder()
                .passengerId(DEFAULT_ID)
                .amount(DEFAULT_AMOUNT)
                .currency(DEFAULT_CURRENCY)
                .build();
    }

    public CustomerChargeResponse getCustomerChargeResponse() {
        return CustomerChargeResponse.builder()
                .id(DEFAULT_CHARGE_ID)
                .passengerId(DEFAULT_ID)
                .currency(DEFAULT_CURRENCY)
                .amount(DEFAULT_AMOUNT)
                .success(DEFAULT_SUCCESS)
                .build();
    }

    public CreateRideRequest getCreateRideByCardRequest() {
        return CreateRideRequest.builder()
                .passengerId(DEFAULT_ID)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .build();
    }

    public CreateRideRequest getCreateRideByCashRequest() {
        return CreateRideRequest.builder()
                .passengerId(DEFAULT_ID)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .distance(DEFAULT_DISTANCE)
                .build();
    }

    public RideResponse getRideResponseByCard() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_ID)
                .car(new CarResponse())
                .driverId(DEFAULT_ID)
                .chargeId(DEFAULT_CHARGE_ID)
                .paymentMethod(PAYMENT_CARD)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATE)
                .endDate(DEFAULT_DATE)
                .distance(DEFAULT_DISTANCE)
                .status(DEFAULT_RIDE_STATUS)
                .build();
    }

    public RideResponse getRideResponseByCash() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_ID)
                .car(new CarResponse())
                .driverId(DEFAULT_ID)
                .chargeId(DEFAULT_CHARGE_ID)
                .paymentMethod(PAYMENT_CASH)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .startDate(DEFAULT_DATE)
                .endDate(DEFAULT_DATE)
                .distance(DEFAULT_DISTANCE)
                .status(DEFAULT_RIDE_STATUS)
                .build();
    }

    public ExceptionResponse getNotFoundExceptionResponse() {
        return getBasicExceptionResponse(new PassengerNotFoundException(), HttpStatus.NOT_FOUND);
    }

    public ExceptionResponse getWrongPageableParameterExceptionResponse() {
        return getBasicExceptionResponse(new WrongPageableParameterException(), HttpStatus.BAD_REQUEST);
    }

    public ExceptionResponse getUsernameAlreadyExistsExceptionResponse() {
        return getBasicExceptionResponse(new UsernameAlreadyExistsException(), HttpStatus.CONFLICT);
    }

    public ExceptionResponse getPhoneAlreadyExistsExceptionResponse() {
        return getBasicExceptionResponse(new PhoneAlreadyExistsException(), HttpStatus.CONFLICT);
    }

    public ExceptionResponse getWrongSortFieldExceptionResponse() {
        return getBasicExceptionResponse(new WrongSortFieldException(), HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse getBasicExceptionResponse(Exception ex, HttpStatus status) {
        return ExceptionResponse.builder()
                .message(ex.getMessage())
                .httpStatus(status)
                .build();
    }
}
