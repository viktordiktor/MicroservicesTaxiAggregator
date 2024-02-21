package com.nikonenko.driverservice.utils;

import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.dto.RatingDriverResponse;
import com.nikonenko.driverservice.dto.RatingToDriverRequest;
import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.models.RatingDriver;
import com.nikonenko.driverservice.models.feign.RidePaymentMethod;
import com.nikonenko.driverservice.models.feign.RideStatus;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
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
    public final Long DEFAULT_SECOND_ID = 2L;
    public final String DEFAULT_USERNAME = "USERNAME1";
    public final String DEFAULT_SECOND_USERNAME = "USERNAME2";
    public final String DEFAULT_PHONE = "+375111111111";
    public final String DEFAULT_SECOND_PHONE = "+375222222222";
    public final String DEFAULT_CAR_NUMBER = "1111-AA1";
    public final String SECOND_CAR_NUMBER = "2222-AA2";
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

    public List<Driver> getDriverList() {
        return List.of(getDefaultDriver(), getDefaultSecondDriver());
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

    public Driver getDefaultSecondDriver() {
        return Driver.builder()
                .id(DEFAULT_SECOND_ID)
                .username(DEFAULT_SECOND_USERNAME)
                .car(getDefaultCar())
                .phone(DEFAULT_SECOND_PHONE)
                .ratingSet(getDefaultRatingSet())
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
                .username(DEFAULT_SECOND_USERNAME)
                .phone(DEFAULT_SECOND_PHONE)
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

    public DriverResponse getUpdateDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_SECOND_USERNAME)
                .car(getDefaultCarResponse())
                .phone(DEFAULT_SECOND_PHONE)
                .ratingSet(getDefaultRatingSetResponse())
                .available(FALSE_AVAILABLE)
                .build();
    }

    public List<DriverResponse> getDriverResponseList(ModelMapper modelMapper) {
        return getDriverList().stream()
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

    public Car getDefaultSecondCar() {
        return Car.builder()
                .id(DEFAULT_SECOND_ID)
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

    public CarResponse getUpdateCarResponse() {
        return CarResponse.builder()
                .id(DEFAULT_ID)
                .number(SECOND_CAR_NUMBER)
                .model(SECOND_CAR_MODEL)
                .color(SECOND_CAR_COLOR)
                .build();
    }

    public List<Car> getDefaultCarList() {
        return List.of(getDefaultCar());
    }

    public List<CarResponse> getCarResponseList(ModelMapper modelMapper) {
        return getDefaultCarList().stream()
                .map(car -> modelMapper.map(car, CarResponse.class))
                .collect(Collectors.toList());
    }

    public Set<RatingDriver> getDefaultRatingSet() {
        return Set.of(new RatingDriver(DEFAULT_ID, DEFAULT_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingDriver(DEFAULT_SECOND_ID, DEFAULT_SECOND_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public Set<RatingDriverResponse> getDefaultRatingSetResponse() {
        return Set.of(new RatingDriverResponse(DEFAULT_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT),
                new RatingDriverResponse(DEFAULT_SECOND_ID, DEFAULT_RATING, DEFAULT_RATING_COMMENT));
    }

    public RatingToDriverRequest getRatingToDriverRequest() {
        return RatingToDriverRequest.builder()
                .driverId(DEFAULT_ID)
                .rating(DEFAULT_RATING)
                .comment(DEFAULT_RATING_COMMENT)
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
}
