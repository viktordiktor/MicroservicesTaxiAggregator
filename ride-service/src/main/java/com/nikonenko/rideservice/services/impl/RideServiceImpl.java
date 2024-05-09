package com.nikonenko.rideservice.services.impl;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.RatingToDriverRequest;
import com.nikonenko.rideservice.dto.RatingToPassengerRequest;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.exceptions.ChargeIsNotSuccessException;
import com.nikonenko.rideservice.exceptions.RideIsNotAcceptedException;
import com.nikonenko.rideservice.exceptions.RideIsNotFinishedException;
import com.nikonenko.rideservice.exceptions.RideIsNotOpenedException;
import com.nikonenko.rideservice.exceptions.RideIsNotStartedException;
import com.nikonenko.rideservice.exceptions.RideNotFoundException;
import com.nikonenko.rideservice.exceptions.UnknownDriverException;
import com.nikonenko.rideservice.kafka.producer.UpdateDriverRatingRequestProducer;
import com.nikonenko.rideservice.kafka.producer.UpdatePassengerRatingRequestProducer;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.RideService;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.utils.LatLngConverter;
import com.nikonenko.rideservice.utils.LogList;
import com.nikonenko.rideservice.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.GeodeticCalculator;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final UpdateDriverRatingRequestProducer updateDriverRatingRequestProducer;
    private final UpdatePassengerRatingRequestProducer updatePassengerRatingRequestProducer;
    private final PaymentService paymentService;
    private final LatLngConverter latLngConverter;

    @Override
    public Mono<RideResponse> getRideById(String rideId) {
        return getOrThrow(rideId)
                .flatMap(ride -> Mono.just(modelMapper.map(ride, RideResponse.class)));
    }

    @Override
    public Flux<RideResponse> getOpenRides(int pageNumber, int pageSize, String sortField) {
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, RideResponse.class);
        return rideRepository.findAllByStatusIs(RideStatus.OPENED, pageable)
                .map(ride -> modelMapper.map(ride, RideResponse.class));
    }

    @Override
    public Flux<RideResponse> getRidesByPassenger(UUID passengerId,
                                                          int pageNumber, int pageSize, String sortField) {
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, RideResponse.class);
        return rideRepository.findAllByPassengerIdIs(passengerId, pageable)
                .map(ride -> modelMapper.map(ride, RideResponse.class));
    }

    @Override
    public Flux<RideResponse> getRidesByDriver(UUID driverId,
                                                       int pageNumber, int pageSize, String sortField) {
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, RideResponse.class);
        return rideRepository.findAllByDriverIdIs(driverId, pageable)
                .map(ride -> modelMapper.map(ride, RideResponse.class));
    }

    @Override
    public Mono<CalculateDistanceResponse> calculateDistance(String startGeoString, String endGeoString) {
        LatLng startGeo = latLngConverter.convert(startGeoString);
        LatLng endGeo = latLngConverter.convert(endGeoString);

        return Mono.fromCallable(() -> {
            GeodeticCalculator geoCalc = new GeodeticCalculator();
            geoCalc.setStartingGeographicPoint(startGeo.lng, startGeo.lat);
            geoCalc.setDestinationGeographicPoint(endGeo.lng, endGeo.lat);

            double distanceInKilometers = geoCalc.getOrthodromicDistance() / 1000;

            return CalculateDistanceResponse.builder()
                    .startGeo(startGeo)
                    .endGeo(endGeo)
                    .distance(distanceInKilometers)
                    .build();
        });
    }

    @Override
    public Mono<RideResponse> createRide(CreateRideRequest createRideRequest) {
        Ride ride = modelMapper.map(createRideRequest, Ride.class);

        if (createRideRequest.getChargeId() != null && !createRideRequest.getChargeId().isEmpty()) {
            return Mono.just(createRideRequest.getChargeId())
                    .flatMap(chargeId -> paymentService.getChargeById(chargeId).isSuccess()
                            ? Mono.just(ride)
                            : Mono.error(new ChargeIsNotSuccessException())
                    )
                    .map(saved -> {
                        saved.setStatus(RideStatus.OPENED);
                        saved.setPaymentMethod(getPaymentMethod(createRideRequest));
                        return saved;
                    })
                    .flatMap(rideRepository::save)
                    .doOnSuccess(saved -> log.info(LogList.LOG_CREATE_RIDE, saved.getId()))
                    .map(saved -> modelMapper.map(saved, RideResponse.class));
        } else {
            ride.setPaymentMethod(RidePaymentMethod.BY_CASH);
            return rideRepository.save(ride)
                    .doOnSuccess(saved -> log.info(LogList.LOG_CREATE_RIDE, saved.getId()))
                    .map(saved -> modelMapper.map(saved, RideResponse.class));
        }
    }

    private RidePaymentMethod getPaymentMethod(CreateRideRequest createRideRequest) {
        return (createRideRequest.getChargeId() == null || createRideRequest.getChargeId().isEmpty())
                ? RidePaymentMethod.BY_CASH
                : RidePaymentMethod.BY_CARD;
    }

    @Override
    public Mono<CloseRideResponse> closeRide(String rideId) {
        return getOrThrow(rideId)
                .flatMap(ride -> {
                    if (ride.getStatus()!= RideStatus.OPENED) {
                        return Mono.error(new RideIsNotOpenedException());
                    }
                    Mono<CustomerChargeReturnResponse> chargeReturnResponseMono;
                    if (ride.getPaymentMethod() == RidePaymentMethod.BY_CARD) {
                        chargeReturnResponseMono = Mono.just(returnCharge(ride));
                    } else {
                        chargeReturnResponseMono = Mono.empty();
                    }
                    return rideRepository.delete(ride)
                            .doOnSuccess(v -> log.info(LogList.LOG_DELETE_RIDE, rideId))
                            .then(chargeReturnResponseMono
                                    .map(chargeReturnResponse -> CloseRideResponse.builder()
                                            .ridePaymentMethod(ride.getPaymentMethod())
                                            .customerChargeReturnResponse(chargeReturnResponse)
                                            .build())
                                    .defaultIfEmpty(CloseRideResponse.builder()
                                            .ridePaymentMethod(ride.getPaymentMethod())
                                            .build()));
                });
    }
    public CustomerChargeReturnResponse returnCharge(Ride ride) {
        return paymentService.returnCharge(ride.getChargeId());
    }

    @Override
    public Mono<Void> changeDriverRating(ReviewRequest request) {
        return getFinishedRide(request)
                .flatMap(ride -> {
                    updateDriverRatingRequestProducer.sendRatingDriverRequest(RatingToDriverRequest.builder()
                            .driverId(ride.getDriverId())
                            .rating(request.getRating())
                            .comment(request.getComment())
                            .build());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> changePassengerRating(ReviewRequest request) {
        return getFinishedRide(request)
                .flatMap(ride -> {
                    updatePassengerRatingRequestProducer.sendRatingPassengerRequest(RatingToPassengerRequest.builder()
                            .passengerId(ride.getPassengerId())
                            .rating(request.getRating())
                            .comment(request.getComment())
                            .build());
                    return Mono.empty();
                });
    }

    private Mono<Ride> getFinishedRide(ReviewRequest request) {
        return getOrThrow(request.getRideId())
                .flatMap(ride -> {
                    if (ride.getStatus() != RideStatus.FINISHED) {
                        return Mono.error(new RideIsNotFinishedException());
                    }
                    return Mono.just(ride);
                });
    }

    @Override
    public Mono<Void> changeRideStatus(ChangeRideStatusRequest request) {
        return getOrThrow(request.getRideId())
                .flatMap(ride -> handleRideAction(ride, request))
                .then();
    }

    private Mono<Void> handleRideAction(Ride ride, ChangeRideStatusRequest request) {
        return switch (request.getRideAction()) {
            case ACCEPT -> acceptRide(ride, request);
            case REJECT -> rejectRide(ride, request);
            case START -> startRide(ride, request);
            case FINISH -> finishRide(ride, request);
        };
    }

    public Mono<Void> acceptRide(Ride ride, ChangeRideStatusRequest request) {
        if (ride.getStatus() != RideStatus.OPENED) {
            return Mono.error(new RideIsNotOpenedException());
        }
        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setCar(request.getCar());
        return saveAndLog(ride, LogList.LOG_ACCEPT_RIDE);
    }

    public Mono<Void> rejectRide(Ride ride, ChangeRideStatusRequest request) {
        return checkRideAttributes(ride, request.getDriverId(), RideStatus.ACCEPTED, new RideIsNotAcceptedException())
                .then(Mono.fromRunnable(() -> {
                    ride.setDriverId(null);
                    ride.setCar(null);
                    ride.setStatus(RideStatus.OPENED);
                }))
                .flatMap(x -> saveAndLog(ride, LogList.LOG_REJECT_RIDE));
    }

    public Mono<Void> startRide(Ride ride, ChangeRideStatusRequest request) {
        return checkRideAttributes(ride, request.getDriverId(), RideStatus.ACCEPTED, new RideIsNotAcceptedException())
                .then(Mono.fromRunnable(() -> {
                    ride.setStartDate(LocalDateTime.now());
                    ride.setStatus(RideStatus.STARTED);
                }))
                .flatMap(x -> saveAndLog(ride, LogList.LOG_START_RIDE));
    }

    public Mono<Void> finishRide(Ride ride, ChangeRideStatusRequest request) {
        return checkRideAttributes(ride, request.getDriverId(), RideStatus.STARTED, new RideIsNotStartedException())
                .then(Mono.fromRunnable(() -> {
                    ride.setEndDate(LocalDateTime.now());
                    ride.setStatus(RideStatus.FINISHED);
                }))
                .flatMap(x -> saveAndLog(ride, LogList.LOG_FINISH_RIDE));
    }

    private Mono<Void> saveAndLog(Ride ride, String logStatus) {
        return rideRepository.save(ride)
                .then(Mono.fromRunnable(() -> log.info(logStatus, ride.getId())));
    }

    public Mono<Void> checkRideAttributes(Ride ride, UUID driverId, RideStatus rideStatus, RuntimeException ex) {
        return Mono.defer(() -> {
            if (ride.getStatus() != rideStatus) {
                return Mono.error(ex);
            }
            if (!Objects.equals(ride.getDriverId(), driverId)) {
                return Mono.error(new UnknownDriverException());
            }
            return Mono.empty();
        });
    }

    public Mono<Ride> getOrThrow(String rideId) {
        return rideRepository.findById(rideId)
                .switchIfEmpty(Mono.error(new RideNotFoundException()));
    }
}
