package com.nikonenko.driverservice.integration;

import com.google.gson.Gson;
import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.DriverRequest;
import com.nikonenko.driverservice.dto.DriverResponse;
import com.nikonenko.driverservice.dto.ExceptionResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.integration.config.ContainerConfiguration;
import com.nikonenko.driverservice.models.Driver;
import com.nikonenko.driverservice.repositories.DriverRepository;
import com.nikonenko.driverservice.utils.TestUtil;
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
public class DriverServiceTest extends ContainerConfiguration {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private ModelMapper modelMapper;
    @LocalServerPort
    private int port;

    @Test
    void givenExistingDriverId_whenFindById_thenReturnDriverResponse() {
        Driver driver = driverRepository.findById(TestUtil.DEFAULT_ID).get();
        DriverResponse response = modelMapper.map(driver, DriverResponse.class);

        DriverResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .get(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenNonExistingDriverId_whenFindById_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .when()
                .get(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenValidParams_whenGetDrivers_thenReturnPageResponseDriverResponse() {
        Page<Driver> driversPage = driverRepository.findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<DriverResponse> responses = TestUtil.getDriverResponseList(modelMapper, driversPage.getContent());

        PageResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        Gson gson = new Gson();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    @Test
    void givenInvalidPageParams_whenGetDrivers_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortByParam_whenGetDrivers_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistingDriver_whenEditDriver_thenReturnDriverResponse() {
        DriverRequest request = TestUtil.getUpdateDriverRequest();
        DriverResponse response = TestUtil.getUpdateDriverResponse();

        DriverResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenDriverWithExistingPhone_whenEditDriver_thenReturnExceptionResponse() {
        DriverRequest request = TestUtil.getDriverRequestWithExistingPhoneRequest();
        ExceptionResponse response = TestUtil.getPhoneAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenDriverWithExistingUsername_whenEditDriver_thenReturnExceptionResponse() {
        DriverRequest request = TestUtil.getDriverRequestWithExistingUsernameRequest();
        ExceptionResponse response = TestUtil.getUsernameAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistingDriver_whenEditDriver_thenReturnExceptionResponse() {
        DriverRequest request = TestUtil.getUpdateDriverRequest();
        ExceptionResponse response = TestUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistingDriver_whenDeleteDriver_thenReturnNoContent() {
        given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .delete(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void givenNonExistingDriver_whenDeleteDriver_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .when()
                .delete(TestUtil.DEFAULT_DRIVERS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistingCarAndExistingDriver_whenAddCarToDriver_thenReturnDriverResponse() {
        CarRequest request = TestUtil.getDefaultCarRequest();
        DriverResponse response = TestUtil.getDriverAddingCarResponse();

        DriverResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenExistingCarAndExistingDriver_whenAddCarToDriver_thenReturnExceptionResponse() {
        CarRequest request = TestUtil.getCarRequestWithExistingNumberRequest();
        ExceptionResponse response = TestUtil.getCarNumberAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.UPDATING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNotExistingDriver_whenAddCarToDriver_thenReturnExceptionResponse() {
        CarRequest request = TestUtil.getUpdateCarRequest();
        ExceptionResponse response = TestUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistingDriverWithCar_whenDeleteCar_thenReturnNoContent() {
        given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .delete(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void givenNonExistingDriver_whenDeleteCar_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .when()
                .delete(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistingDriverWithoutCar_whenDeleteCar_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getCarNotFoundExceptionResponse();

        driverRepository.save(TestUtil.getCreationDriver());

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.CREATION_ID)
                .when()
                .delete(TestUtil.DEFAULT_DRIVER_CAR_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
        //@Order(1)
    void givenNonExistingDriver_whenCreateDriver_thenReturnDriverResponse() {
        DriverResponse response = TestUtil.getCreationDriverResponse();
        DriverRequest request = TestUtil.getCreationDriverRequest();

        DriverResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenDriverWithExistingPhone_whenCreateDriver_thenReturnExceptionResponse() {
        DriverRequest request = TestUtil.getDriverRequestWithExistingPhoneRequest();
        ExceptionResponse response = TestUtil.getPhoneAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenDriverWithExistingUsername_whenCreateDriver_thenReturnExceptionResponse() {
        DriverRequest request = TestUtil.getDriverRequestWithExistingUsernameRequest();
        ExceptionResponse response = TestUtil.getUsernameAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(TestUtil.DEFAULT_DRIVERS_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }
}
