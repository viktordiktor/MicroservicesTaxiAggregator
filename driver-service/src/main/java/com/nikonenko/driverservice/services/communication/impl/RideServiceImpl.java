package com.nikonenko.driverservice.services.communication.impl;

import com.nikonenko.driverservice.dto.feign.rides.RideResponse;
import com.nikonenko.driverservice.models.feign.RidePaymentMethod;
import com.nikonenko.driverservice.models.feign.RideStatus;
import com.nikonenko.driverservice.services.communication.RideService;
import com.nikonenko.driverservice.utils.LogList;
import com.nikonenko.driverservice.utils.RequestInterceptUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
    public Flux<RideResponse> getRidesByDriverId(UUID driverId,
                                                 int pageNumber, int pageSize, String sortField) {
        return webClient.get()
                .uri(builder -> builder.path("/api/v1/rides/by-driver/{driverId}")
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortField", sortField)
                        .build(driverId))
                .header(HttpHeaders.AUTHORIZATION, RequestInterceptUtil.getAuthorizationHeader())
                .retrieve().bodyToFlux(RideResponse.class);
    }

    public Flux<RideResponse> fallbackRideService(UUID driverId, int pageNumber, int pageSize, Exception ex) {
        log.error(LogList.LOG_GET_RIDES_BY_DRIVER_ID_FEIGN_ERROR, driverId, ex.getMessage());
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
