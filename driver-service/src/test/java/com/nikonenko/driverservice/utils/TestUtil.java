package com.nikonenko.driverservice.utils;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.ExceptionResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingDriverResponse;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.exceptions.CarNotFoundException;
import com.nikonenko.driverservice.exceptions.CarNumberAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.DriverNotFoundException;
import com.nikonenko.driverservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.driverservice.exceptions.WrongPageableParameterException;
import com.nikonenko.driverservice.exceptions.WrongSortFieldException;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.models.RatingDriver;
import com.nikonenko.driverservice.models.feign.RidePaymentMethod;
import com.nikonenko.driverservice.models.feign.RideStatus;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class TestUtil {
    public final int DEFAULT_PAGE = 0;
    public final int INVALID_PAGE = -1;
    public final int DEFAULT_PAGE_SIZE = 5;
    public final int INVALID_PAGE_SIZE = 0;
    public final String DEFAULT_PAGE_SORT = "id";
    public final String INVALID_PAGE_SORT = "aaa";
    public final int DEFAULT_TOTAL_PAGE_SIZE = 1;
    public final Long DEFAULT_ID = 1L;
    public final Long SECOND_ID = 2L;
    public final Long UPDATING_ID = 3L;
    public final Long CREATION_ID = 4L;
    public final Long NOT_EXISTING_ID = 999L;
    public final String DEFAULT_USERNAME = "viktordiktor";
    public final String SECOND_USERNAME = "USERNAME2";
    public final String EXISTING_USERNAME = "FANAT_UBER";
    public final String DEFAULT_PHONE = "+375111111111";
    public final String SECOND_PHONE = "+375232323232";
    public final String EXISTING_PHONE = "+375123456789";
    public final String DEFAULT_CAR_NUMBER = "1111AA1";
    public final String SECOND_CAR_NUMBER = "2222AA2";
    public final String EXISTING_CAR_NUMBER = "1234AA1";
    public final String DEFAULT_CAR_MODEL = "Model1";
    public final String SECOND_CAR_MODEL = "Model2";
    public final String DEFAULT_CAR_COLOR = "Color1";
    public final String SECOND_CAR_COLOR = "Color2";
    public final Integer DEFAULT_RATING = 5;
    public final String DEFAULT_RATING_COMMENT = "Comment1";
    public final boolean FALSE_AVAILABLE = false;
    public final boolean TRUE_AVAILABLE = true;
    public final String DEFAULT_CHARGE_ID = "charge1";
    public final String DEFAULT_RIDE_ID = "ride1";
    public final Double DEFAULT_DISTANCE = 0.0;
    public final LocalDateTime DEFAULT_DATE = LocalDateTime.now();
    public final String DEFAULT_START_ADDRESS = "address1";
    public final String DEFAULT_END_ADDRESS = "address2";
    public final RideStatus DEFAULT_RIDE_STATUS = RideStatus.OPENED;
    public final RidePaymentMethod DEFAULT_PAYMENT_METHOD = RidePaymentMethod.BY_CARD;
    public final String DEFAULT_CARS_ID_PATH = "/api/v1/cars/{id}";
    public final String DEFAULT_CARS_PATH = "/api/v1/cars";
    public final String DEFAULT_DRIVERS_ID_PATH = "/api/v1/drivers/{id}";
    public final String DEFAULT_DRIVER_CAR_PATH = "/api/v1/drivers/car/{id}";
    public final String DEFAULT_DRIVERS_PATH = "/api/v1/drivers";
    public final String ID_PARAMETER = "id";
    public final String PAGE_NUMBER_PARAMETER = "pageNumber";
    public final String PAGE_SIZE_PARAMETER = "pageSize";
    public final String SORT_FIELD_PARAMETER = "sortField";

    public List<Driver> getDriverList() {
        return List.of(getDefaultDriver(), getSecondDriver());
    }

    public Driver getDefaultDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .car(getDefaultCar())
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public Driver getSecondDriver() {
        return Driver.builder()
                .id(SECOND_ID)
                .username(SECOND_USERNAME)
                .car(getDefaultCar())
                .phone(SECOND_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public Driver getCreationDriver() {
        return Driver.builder()
                .id(CREATION_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .available(FALSE_AVAILABLE)
                .build();
    }

    public DriverRequest getDriverRequest() {
        return DriverRequest.builder()
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverRequest getUpdateDriverRequest() {
        return DriverRequest.builder()
                .username(SECOND_USERNAME)
                .phone(SECOND_PHONE)
                .build();
    }

    public Driver getNotSavedDriver() {
        return Driver.builder()
                .username(DEFAULT_USERNAME)
                .car(getDefaultCar())
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public Driver getAvailableDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .car(getDefaultCar())
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(TRUE_AVAILABLE)
                .build();
    }

    public Driver getNotAvailableDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .car(getDefaultCar())
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public Driver getAvailableDriverWithoutCar() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(TRUE_AVAILABLE)
                .build();
    }

    public Driver getNotAvailableDriverWithoutCar() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSet())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .car(getDefaultCarResponse())
                .phone(DEFAULT_PHONE)
                .ratingSet(getDefaultRatingSetResponse())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public DriverResponse getDriverResponseWithCarParameters(String number, String model, String color) {
        DriverResponse driverResponse = getDefaultDriverResponse();
        driverResponse.setCar(getCarResponseWithParameters(number, model, color));
        return driverResponse;
    }

    public DriverResponse getUpdateDriverResponse() {
        return DriverResponse.builder()
                .id(UPDATING_ID)
                .username(SECOND_USERNAME)
                .phone(SECOND_PHONE)
                .ratingSet(new HashSet<>())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public DriverRequest getCreationDriverRequest() {
        return DriverRequest.builder()
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverRequest getDriverRequestWithParameters(String username, String phone) {
        return DriverRequest.builder()
                .username(username)
                .phone(phone)
                .build();
    }

    public DriverResponse getCreationDriverResponse() {
        return DriverResponse.builder()
                .id(CREATION_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .available(true)
                .build();
    }

    public DriverRequest getDriverRequestWithExistingPhoneRequest() {
        return DriverRequest.builder()
                .username(DEFAULT_USERNAME)
                .phone(EXISTING_PHONE)
                .build();
    }

    public DriverRequest getDriverRequestWithExistingUsernameRequest() {
        return DriverRequest.builder()
                .username(EXISTING_USERNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverResponse getDriverAddingCarResponse() {
        return DriverResponse.builder()
                .id(UPDATING_ID)
                .username(EXISTING_USERNAME)
                .phone(EXISTING_PHONE)
                .car(getCreationCarResponse())
                .ratingSet(new HashSet<>())
                .available(true)
                .build();
    }

    public List<DriverResponse> getDriverResponseList(ModelMapper modelMapper, List<Driver> driverList) {
        return driverList.stream()
                .map(driver -> modelMapper.map(driver, DriverResponse.class))
                .collect(Collectors.toList());
    }

    public Car getDefaultCar() {
        return Car.builder()
                .id(DEFAULT_ID)
                .number(DEFAULT_CAR_NUMBER)
                .model(DEFAULT_CAR_MODEL)
                .color(DEFAULT_CAR_COLOR)
                .build();
    }

    public Car getSavedCarWithParameters(String number, String model, String color) {
        return Car.builder()
                .id(DEFAULT_ID)
                .number(number)
                .model(model)
                .color(color)
                .build();
    }

    public Car getSecondCar() {
        return Car.builder()
                .id(SECOND_ID)
                .number(SECOND_CAR_NUMBER)
                .model(SECOND_CAR_MODEL)
                .color(SECOND_CAR_COLOR)
                .build();
    }

    public Car getNotSavedCar() {
        return Car.builder()
                .number(DEFAULT_CAR_NUMBER)
                .model(DEFAULT_CAR_MODEL)
                .color(DEFAULT_CAR_COLOR)
                .build();
    }

    public CarRequest getDefaultCarRequest() {
        return CarRequest.builder()
                .color(DEFAULT_CAR_COLOR)
                .model(DEFAULT_CAR_MODEL)
                .number(DEFAULT_CAR_NUMBER)
                .build();
    }

    public CarRequest getCarRequestWithParameters(String number, String model, String color) {
        return CarRequest.builder()
                .color(number)
                .model(model)
                .number(color)
                .build();
    }

    public CarRequest getCarRequestWithNumber(String number) {
        CarRequest carRequest = getDefaultCarRequest();
        carRequest.setNumber(number);
        return carRequest;
    }

    public CarRequest getUpdateCarRequest() {
        return CarRequest.builder()
                .color(SECOND_CAR_COLOR)
                .model(SECOND_CAR_MODEL)
                .number(SECOND_CAR_NUMBER)
                .build();
    }

    public CarResponse getDefaultCarResponse() {
        return CarResponse.builder()
                .id(DEFAULT_ID)
                .number(DEFAULT_CAR_NUMBER)
                .model(DEFAULT_CAR_MODEL)
                .color(DEFAULT_CAR_COLOR)
                .build();
    }

    public CarResponse getCarResponseWithParameters(String number, String model, String color) {
        return CarResponse.builder()
                .id(DEFAULT_ID)
                .number(number)
                .model(model)
                .color(color)
                .build();
    }

    public CarResponse getUpdateCarResponse() {
        return CarResponse.builder()
                .id(DEFAULT_ID)
                .number(SECOND_CAR_NUMBER)
                .model(SECOND_CAR_MODEL)
                .color(SECOND_CAR_COLOR)
                .build();
    }

    public CarResponse getCreationCarResponse() {
        return CarResponse.builder()
                .id(CREATION_ID)
                .number(DEFAULT_CAR_NUMBER)
                .model(DEFAULT_CAR_MODEL)
                .color(DEFAULT_CAR_COLOR)
                .build();
    }

    public List<Car> getDefaultCarList() {
        return List.of(getDefaultCar());
    }

    public List<CarResponse> getCarResponseList(ModelMapper modelMapper, List<Car> carList) {
        return carList.stream()
                .map(car -> modelMapper.map(car, CarResponse.class))
                .collect(Collectors.toList());
    }

    public CarRequest getCarRequestWithExistingNumberRequest() {
        return CarRequest.builder()
                .number(EXISTING_CAR_NUMBER)
                .model(DEFAULT_CAR_MODEL)
                .color(DEFAULT_CAR_COLOR)
                .build();
    }

    public Set<RatingDriver> getDefaultRatingSet() {
        return Set.of(new RatingDriver(DEFAULT_ID, DEFAULT_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingDriver(SECOND_ID, SECOND_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public Set<RatingDriverResponse> getDefaultRatingSetResponse() {
        return Set.of(new RatingDriverResponse(DEFAULT_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingDriverResponse(SECOND_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public RatingToDriverRequest getRatingToDriverRequest() {
        return RatingToDriverRequest.builder()
                .driverId(DEFAULT_ID)
                .rating(DEFAULT_RATING)
                .comment(DEFAULT_RATING_COMMENT)
                .build();
    }

    public RatingToDriverRequest getRatingToDriverRequestWithParameters(Long driverId, int rating, String comment) {
        return RatingToDriverRequest.builder()
                .driverId(driverId)
                .rating(rating)
                .comment(comment)
                .build();
    }

    public List<RideResponse> getRideResponseList() {
        return List.of(getRideResponse());
    }

    public RideResponse getRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .car(getDefaultCarResponse())
                .driverId(DEFAULT_ID)
                .passengerId(DEFAULT_ID)
                .chargeId(DEFAULT_CHARGE_ID)
                .distance(DEFAULT_DISTANCE)
                .startDate(DEFAULT_DATE)
                .endDate(DEFAULT_DATE)
                .startAddress(DEFAULT_START_ADDRESS)
                .endAddress(DEFAULT_END_ADDRESS)
                .status(DEFAULT_RIDE_STATUS)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .build();
    }

    public PageResponse<RideResponse> getPageRideResponse() {
        return PageResponse.<RideResponse>builder()
                .objectList(getRideResponseList())
                .totalElements(getRideResponseList().size())
                .totalPages(DEFAULT_TOTAL_PAGE_SIZE)
                .build();
    }

    public ExceptionResponse getCarNotFoundExceptionResponse() {
        return getBasicExceptionResponse(new CarNotFoundException(), HttpStatus.NOT_FOUND);
    }

    public ExceptionResponse getDriverNotFoundExceptionResponse() {
        return getBasicExceptionResponse(new DriverNotFoundException(), HttpStatus.NOT_FOUND);
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

    public ExceptionResponse getCarNumberAlreadyExistsExceptionResponse() {
        return getBasicExceptionResponse(new CarNumberAlreadyExistsException(), HttpStatus.CONFLICT);
    }

    private ExceptionResponse getBasicExceptionResponse(Exception ex, HttpStatus status) {
        return ExceptionResponse.builder()
                .message(ex.getMessage())
                .httpStatus(status)
                .build();
    }
}
