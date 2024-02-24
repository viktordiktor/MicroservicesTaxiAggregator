package com.nikonenko.passengerservice.integration;

import com.google.gson.Gson;
import com.nikonenko.passengerservice.dto.ExceptionResponse;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.integration.config.ContainerConfiguration;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import com.nikonenko.passengerservice.utils.TestUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Sql(
        scripts = {
                "classpath:sql/delete-data.sql",
                "classpath:sql/insert-data.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class PassengerServiceTest extends ContainerConfiguration {
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @LocalServerPort
    private int port;

    @Test
    void givenExistsPassengerId_whenFindById_thenReturnPassengerResponse() {
        Passenger passenger = passengerRepository.findById(TestUtil.DEFAULT_ID).get();
        PassengerResponse response = modelMapper.map(passenger, PassengerResponse.class);

        PassengerResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .get(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenNonExistsPassengerId_whenFindById_thenThrowException() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
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
    void givenValidPageParams_whenFindAll_thenReturnPagePassengerResponse() {
        Page<Passenger> passengersPage = passengerRepository.findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<PassengerResponse> responses = TestUtil.getPassengerResponseList(modelMapper, passengersPage.getContent());

        PageResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        Gson gson = new Gson();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    @Test
    void givenInvalidPageParams_whenFindAll_thenThrowException() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortField_whenFindAll_thenThrowException() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistsPassenger_whenCreatePassenger_thenReturnPassengerResponse() {
        PassengerResponse response = TestUtil.getCreationPassengerResponse();
        PassengerRequest request = TestUtil.getCreationPassengerRequest();

        PassengerResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenPassengerWithExistingPhone_whenCreatePassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getPassengerRequestWithExistingPhoneRequest();
        ExceptionResponse response = TestUtil.getPhoneAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenPassengerWithExistingUsername_whenCreatePassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getPassengerRequestWithExistingUsernameRequest();
        ExceptionResponse response = TestUtil.getUsernameAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistsPassengerIdAndValidPassenger_whenEditPassenger_thenReturnPassengerResponse() {
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();
        PassengerResponse response = TestUtil.getUpdatePassengerResponse();

        PassengerResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenExistsPassengerIdAndPassengerWithExistingPhone_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getPassengerRequestWithExistingPhoneRequest();
        ExceptionResponse response = TestUtil.getPhoneAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistsPassengerIdAndPassengerWithExistingUsername_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getPassengerRequestWithExistingUsernameRequest();
        ExceptionResponse response = TestUtil.getUsernameAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistsPassengerId_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistsPassengerId_whenDeletePassenger_thenReturnNoContent() {
        given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .delete(TestUtil.DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void givenNonExistsPassengerId_whenDeletePassenger_thenThrowException() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
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
