package com.nikonenko.driverservice.integration;

import com.google.gson.Gson;
import com.nikonenko.driverservice.dto.CarRequest;
import com.nikonenko.driverservice.dto.CarResponse;
import com.nikonenko.driverservice.dto.ExceptionResponse;
import com.nikonenko.driverservice.dto.PageResponse;
import com.nikonenko.driverservice.integration.config.ContainerConfiguration;
import com.nikonenko.driverservice.models.Car;
import com.nikonenko.driverservice.repositories.CarRepository;
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
public class CarServiceTest extends ContainerConfiguration {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ModelMapper modelMapper;
    @LocalServerPort
    private int port;

    @Test
    void givenExistingCarId_whenFindById_thenReturnCarResponse() {
        Car car = carRepository.findById(TestUtil.DEFAULT_ID).get();
        CarResponse response = modelMapper.map(car, CarResponse.class);

        CarResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .when()
                .get(TestUtil.DEFAULT_CARS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenNonExistingCarId_whenFindById_thenThrowException() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .when()
                .get(TestUtil.DEFAULT_CARS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenValidParams_whenGetCars_thenReturnPageResponseCarResponse() {
        Page<Car> carsPage = carRepository.findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<CarResponse> responses = TestUtil.getCarResponseList(modelMapper, carsPage.getContent());

        PageResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_CARS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PageResponse.class);

        Gson gson = new Gson();
        assertEquals(gson.toJson(responses), gson.toJson(result.getObjectList()));
    }

    @Test
    void givenInvalidPageParams_whenGetCars_thenThrowException() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_CARS_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenInvalidSortByParam_whenGetCars_thenThrowException() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                .when()
                .get(TestUtil.DEFAULT_CARS_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenExistingCar_whenEditCar_thenUpdateCar() {
        CarRequest request = TestUtil.getUpdateCarRequest();
        CarResponse response = TestUtil.getUpdateCarResponse();

        CarResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_CARS_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertEquals(response, result);
    }

    @Test
    void givenCarWithExistingNumber_whenEditCar_thenThrowException() {
        CarRequest request = TestUtil.getCarRequestWithExistingNumberRequest();
        ExceptionResponse response = TestUtil.getPhoneAlreadyExistsExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.DEFAULT_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_CARS_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }

    @Test
    void givenNonExistingCar_whenEditCar_thenThrowException() {
        CarRequest request = TestUtil.getUpdateCarRequest();
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        ExceptionResponse result = given()
                .port(port)
                .pathParam(TestUtil.ID_PARAMETER, TestUtil.NOT_EXISTING_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(TestUtil.DEFAULT_CARS_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertEquals(response.getHttpStatus(), result.getHttpStatus());
        assertEquals(response.getMessage(), result.getMessage());
    }
}
