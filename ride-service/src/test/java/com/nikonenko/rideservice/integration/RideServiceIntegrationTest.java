package com.nikonenko.rideservice.integration;

import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.ExceptionResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.integration.config.ContainerConfiguration;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.utils.TestUtil;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class RideServiceIntegrationTest extends ContainerConfiguration {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private WebTestClient webClient;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("rides");
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

    @Test
    void givenExistsRideId_whenFindById_thenReturnRideResponse() {
        Ride ride = rideRepository.findById(TestUtil.DEFAULT_RIDE_ID).block();
        RideResponse response = modelMapper.map(ride, RideResponse.class);

        webClient.get()
                .uri(TestUtil.DEFAULT_ID_PATH, TestUtil.DEFAULT_RIDE_ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(RideResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenNonExistsPassengerId_whenFindById_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        webClient.get()
                .uri(TestUtil.DEFAULT_ID_PATH, TestUtil.NOT_EXISTING_RIDE_ID)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ExceptionResponse.class)
                .consumeWith(result -> {
                    assertEquals(response.getHttpStatus(), result.getResponseBody().getHttpStatus());
                    assertEquals(response.getMessage(), result.getResponseBody().getMessage());
                });
    }

    @Test
    void givenValidPageParams_whenFindByPassenger_thenReturnPageRideResponse() {
        Flux<Ride> ridesPage = rideRepository.findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<Ride> rideList = ridesPage.collectList().block();
        List<RideResponse> responses = TestUtil.getRideResponseList(rideList, modelMapper);

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build(TestUtil.DEFAULT_PASSENGER_ID))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RideResponse.class)
                .isEqualTo(responses);
    }

    @Test
    void givenInvalidPageParams_whenFindByPassenger_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build(TestUtil.DEFAULT_PASSENGER_ID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenInvalidSortField_whenFindByPassenger_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_PASSENGER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                        .build(TestUtil.DEFAULT_PASSENGER_ID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenValidPageParams_whenFindByDriver_thenReturnPageRideResponse() {
        Flux<Ride> ridesPage = rideRepository.findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<Ride> rideList = ridesPage.collectList().block();
        List<RideResponse> responses = TestUtil.getRideResponseList(rideList, modelMapper);

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_DRIVER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build(TestUtil.DEFAULT_PASSENGER_ID))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RideResponse.class)
                .isEqualTo(responses);
    }

    @Test
    void givenInvalidPageParams_whenFindByDriver_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_DRIVER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build(TestUtil.DEFAULT_DRIVER_ID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenInvalidSortField_whenFindByDriver_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_BY_DRIVER_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                        .build(TestUtil.DEFAULT_DRIVER_ID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenValidPageParams_whenFindOpenRides_thenReturnPageRideResponse() {
        Flux<Ride> ridesFlux = rideRepository.findAllByStatusIs(TestUtil.OPENED_STATUS,
                PageRequest.of(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        List<RideResponse> responses = TestUtil.getRideResponseList(ridesFlux.collectList().block(), modelMapper);

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_OPEN_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RideResponse.class)
                .isEqualTo(responses);
    }

    @Test
    void givenInvalidPageParams_whenFindOpenRides_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongPageableParameterExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_OPEN_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.INVALID_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.INVALID_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.DEFAULT_PAGE_SORT)
                        .build())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenInvalidSortField_whenFindOpenRides_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getWrongSortFieldExceptionResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_OPEN_PATH)
                        .queryParam(TestUtil.PAGE_NUMBER_PARAMETER, TestUtil.DEFAULT_PAGE)
                        .queryParam(TestUtil.PAGE_SIZE_PARAMETER, TestUtil.DEFAULT_PAGE_SIZE)
                        .queryParam(TestUtil.SORT_FIELD_PARAMETER, TestUtil.INVALID_PAGE_SORT)
                        .build())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenNonExistingRideByCash_whenCreateRide_thenReturnRideResponse() {
        CreateRideRequest request = TestUtil.getCreateRideRequestByCash();
        RideResponse response = TestUtil.getCreateRideResponseByCash();

        webClient.post()
                .uri(TestUtil.DEFAULT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(RideResponse.class)
                .value(result -> TestUtil.assertEqualsAllRideResponseFieldsWithoutId(response, result));
    }

    @Test
    void givenCalculateDistanceRequest_whenCalculateDistance_thenReturnCalculateDistanceResponse() {
        CalculateDistanceResponse response = TestUtil.getCalculateDistanceResponse();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_DISTANCE_PATH)
                        .queryParam(TestUtil.START_GEO_PARAMETER, TestUtil.DEFAULT_START_LATLNG.toString())
                        .queryParam(TestUtil.END_GEO_PARAMETER, TestUtil.DEFAULT_END_LATLNG.toString())
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CalculateDistanceResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenRideIdByCard_whenCloseRide_thenReturnNoContent() {
        webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_ID_PATH)
                        .build(TestUtil.DEFAULT_RIDE_ID))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void giveNotOpenedRideId_whenCloseRide_thenReturnExceptionResponse() {
        Ride ride = rideRepository.findById(TestUtil.DEFAULT_RIDE_ID).block();
        ride.setStatus(RideStatus.FINISHED);
        rideRepository.save(ride);
        ExceptionResponse response = TestUtil.getRideIsNotOpenedExceptionResponse();

        webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_ID_PATH)
                        .build(TestUtil.DEFAULT_RIDE_ID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionResponse.class)
                .value(result -> {
                    assertEquals(response.getHttpStatus(), result.getHttpStatus());
                    assertEquals(response.getMessage(), result.getMessage());
                });
    }

    @Test
    void giveNonExistingRideId_whenCloseRide_thenReturnExceptionResponse() {
        ExceptionResponse response = TestUtil.getNotFoundExceptionResponse();

        webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtil.DEFAULT_ID_PATH)
                        .build(TestUtil.NOT_EXISTING_RIDE_ID))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ExceptionResponse.class)
                .isEqualTo(response);
    }
}
