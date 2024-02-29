package com.nikonenko.rideservice.integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.ExceptionResponse;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.integration.config.ContainerConfiguration;
import com.nikonenko.rideservice.integration.config.LocalDateTimeAdapter;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.utils.TestUtil;
import io.restassured.http.ContentType;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class RideServiceTest extends ContainerConfiguration {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private ModelMapper modelMapper;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        Document rideDocument = new Document()
                .append("_id", TestUtil.DEFAULT_RIDE_ID)
                .append("driverId", TestUtil.DEFAULT_DRIVER_ID)
                .append("passengerId", TestUtil.DEFAULT_PASSENGER_ID)
                .append("startAddress", TestUtil.DEFAULT_START_ADDRESS)
                .append("endAddress", TestUtil.DEFAULT_END_ADDRESS)
                .append("startDate", TestUtil.DEFAULT_DATETIME)
                .append("endDate", TestUtil.DEFAULT_DATETIME)
                .append("chargeId", null)
                .append("distance", TestUtil.DEFAULT_DISTANCE)
                .append("status", RideStatus.OPENED.name())
                .append("ridePaymentMethod", RidePaymentMethod.BY_CASH.name())
                .append("car", TestUtil.DEFAULT_CAR_RESPONSE);
        mongoTemplate.createCollection("rides")
                .insertOne(rideDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection("rides");
    }

    @Test
    void givenExistsRideId_whenFindById_thenReturnRideResponse() {
        Ride ride = rideRepository.findById(TestUtil.DEFAULT_RIDE_ID).get();
        RideResponse response = modelMapper.map(ride, RideResponse.class);

        RideResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_RIDE_ID)
                .when()
                .get(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenNonExistsPassengerId_whenFindById_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_RIDE_ID)
                .when()
                .get(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenValidPageParams_whenFindByPassenger_thenReturnPageRideResponse() {
        Page<Ride> ridesPage = rideRepository.findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<RideResponse> responses = TestUtil.getRideResponseList(ridesPage.getContent(), modelMapper);

        PageResponse result = given()
                .port(port)
                .pathParam(TestUtil.PASSENGER_ID_PARAMETER, TestUtil.DEFAULT_PASSENGER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    @Test
    void givenInvalidPageParams_whenFindByPassenger_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.PASSENGER_ID_PARAMETER, TestUtil.DEFAULT_PASSENGER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortField_whenFindByPassenger_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.PASSENGER_ID_PARAMETER, TestUtil.DEFAULT_PASSENGER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenValidPageParams_whenFindByDriver_thenReturnPageRideResponse() {
        Page<Ride> ridesPage = rideRepository.findAllByDriverIdIs(TestUtil.DEFAULT_PASSENGER_ID,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<RideResponse> responses = TestUtil.getRideResponseList(ridesPage.getContent(), modelMapper);

        PageResponse result = given()
                .port(port)
                .pathParam(TestUtil.DRIVER_ID_PARAMETER, TestUtil.DEFAULT_DRIVER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_DRIVER_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    @Test
    void givenInvalidPageParams_whenFindByDriver_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.DRIVER_ID_PARAMETER, TestUtil.DEFAULT_DRIVER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_DRIVER_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortField_whenFindByDriver_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.DRIVER_ID_PARAMETER, TestUtil.DEFAULT_DRIVER_ID)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_BY_DRIVER_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenValidPageParams_whenFindOpenRides_thenReturnPageRideResponse() {
        Page<Ride> ridesPage = rideRepository.findAllByStatusIs(TestUtil.OPENED_STATUS,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<RideResponse> responses = TestUtil.getRideResponseList(ridesPage.getContent(), modelMapper);

        PageResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_OPEN_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        TestUtil.assertEqualsJsonPageResponse(responses, result);
    }

    @Test
    void givenInvalidPageParams_whenFindOpenRides_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_OPEN_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortField_whenFindOpenRides_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_OPEN_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistingRideByCash_whenCreateRide_thenReturnRideResponse() {
        CreateRideRequest request = TestUtil.getCreateRideRequestByCash();
        RideResponse response = TestUtil.getCreateRideResponseByCash();

        RideResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RideResponse.class);

        TestUtil.assertEqualsAllRideResponseFieldsWithoutId(response, result);
    }

    @Test
    void givenCalculateDistanceRequest_whenCalculateDistance_thenReturnCalculateDistanceResponse() {
        CalculateDistanceResponse response = TestUtil.getCalculateDistanceResponse();

        CalculateDistanceResponse result = given()
                .port(port)
                .queryParam(TestUtil.START_GEO_PARAMETER, TestUtil.DEFAULT_START_LATLNG.toString())
                .queryParam(TestUtil.END_GEO_PARAMETER, TestUtil.DEFAULT_END_LATLNG.toString())
                .when()
                .get(TestUtil.DEFAULT_DISTANCE_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CalculateDistanceResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenRideIdByCard_whenCloseRide_thenReturnNoContent() {
        given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_RIDE_ID)
                .when()
                .delete(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void giveNotOpenedRideId_whenCloseRide_thenReturnExceptionResponse() {
        Ride ride = rideRepository.findById(TestUtil.DEFAULT_RIDE_ID).get();
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);
        ExceptionResponse response = TestUtil.getRideIsNotOpenedExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_RIDE_ID)
                .when()
                .delete(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void giveNonExistingRideId_whenCloseRide_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_RIDE_ID)
                .when()
                .delete(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }
}
