package com.nikonenko.passengerservice.services.communication.impl;

import com.google.maps.model.LatLng;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeReturnResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.models.feign.RideStatus;
import com.nikonenko.passengerservice.services.communication.RideService;
import com.nikonenko.passengerservice.utils.LogList;
import com.nikonenko.passengerservice.utils.RequestInterceptUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
@Retry(name = "rideRetry")
@Slf4j
public class RideServiceImpl implements RideService {
    private final WebClient webClient;

    public RideServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    @CircuitBreaker(name = "rideBreaker", fallbackMethod = "fallbackRideService")
    public CalculateDistanceResponse getRideDistance(CalculateDistanceRequest calculateDistanceRequest) {
        return webClient.get()
                .uri(builder -> builder.path("/api/v1/rides/distance")
                        .queryParam("startGeo", calculateDistanceRequest.getStartGeo())
                        .queryParam("endGeo", calculateDistanceRequest.getEndGeo())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, RequestInterceptUtil.getAuthorizationHeader())
                .retrieve().bodyToMono(CalculateDistanceResponse.class)
                .block();
    }

    @Override
    public Mono<RideResponse> createRide(CreateRideRequest createRideRequest) {
        return webClient.post()
                .uri(builder -> builder.path("/api/v1/rides").build())
                .header(HttpHeaders.AUTHORIZATION, RequestInterceptUtil.getAuthorizationHeader())
                .body(BodyInserters.fromValue(createRideRequest))
                .retrieve().bodyToMono(RideResponse.class);
    }

    @Override
    public Mono<CloseRideResponse> closeRide(String rideId) {
        return webClient.delete()
                .uri(builder -> builder.path("/api/v1/rides/{rideId}").build(rideId))
                .header(HttpHeaders.AUTHORIZATION, RequestInterceptUtil.getAuthorizationHeader())
                .retrieve().bodyToMono(CloseRideResponse.class);
    }

    @Override
    public Flux<RideResponse> getRidesByPassengerId(UUID passengerId, int pageNumber, int pageSize, String sortField) {
        return webClient.get()
                .uri(builder -> builder.path("/api/v1/rides/by-passenger/{passengerId}")
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortField", sortField)
                        .build(passengerId))
                .header(HttpHeaders.AUTHORIZATION, RequestInterceptUtil.getAuthorizationHeader())
                .retrieve().bodyToFlux(RideResponse.class);
    }

    public CalculateDistanceResponse fallbackRideService(CalculateDistanceRequest calculateDistanceRequest, Exception ex) {
        log.error(LogList.LOG_GET_RIDE_DISTANCE_FEIGN_ERROR, ex.getMessage());
        return CalculateDistanceResponse.builder()
                .distance(0.0)
                .startGeo(new LatLng())
                .endGeo(new LatLng())
                .errorMessage(ex.getMessage())
                .build();
    }

    public Mono<RideResponse> fallbackRideService(CreateRideRequest createRideRequest, Exception ex) {
        log.error(LogList.LOG_CREATE_RIDE_FEIGN_ERROR, createRideRequest.getPassengerId(), ex.getMessage());
        return Mono.just(RideResponse.builder()
                .chargeId("")
                .distance(0.0)
                .passengerId(UUID.randomUUID())
                .driverId(UUID.randomUUID())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .paymentMethod(RidePaymentMethod.BY_CASH)
                .status(RideStatus.FINISHED)
                .errorMessage(ex.getMessage())
                .build());
    }

    public Mono<CloseRideResponse> fallbackRideService(String rideId, Exception ex) {
        log.error(LogList.LOG_CLOSE_RIDE_FEIGN_ERROR, rideId, ex.getMessage());
        return Mono.just(CloseRideResponse.builder()
                .customerChargeReturnResponse(new CustomerChargeReturnResponse())
                .ridePaymentMethod(RidePaymentMethod.BY_CASH)
                .errorMessage(ex.getMessage())
                .build());
    }

    public Flux<RideResponse> fallbackRideService(UUID passengerId, int pageNumber, int pageSize, String sortField,
                                                  Exception ex) {
        log.error(LogList.LOG_GET_RIDES_BY_PASSENGER_ID_FEIGN_ERROR, passengerId, ex.getMessage());
        return Flux.just(RideResponse.builder()
                .chargeId("")
                .distance(0.0)
                .passengerId(UUID.randomUUID())
                .driverId(UUID.randomUUID())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .paymentMethod(RidePaymentMethod.BY_CASH)
                .status(RideStatus.FINISHED)
                .errorMessage(ex.getMessage())
                .build());
    }
}
