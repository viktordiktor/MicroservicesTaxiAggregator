package com.nikonenko.rideservice.services.impl;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.PageResponse;
import com.nikonenko.rideservice.dto.RatingToPassengerRequest;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RatingToDriverRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.exceptions.ChargeIsNotSuccessException;
import com.nikonenko.rideservice.exceptions.RideIsNotAcceptedException;
import com.nikonenko.rideservice.exceptions.RideIsNotOpenedException;
import com.nikonenko.rideservice.exceptions.RideIsNotStartedException;
import com.nikonenko.rideservice.exceptions.RideNotFoundException;
import com.nikonenko.rideservice.exceptions.UnknownDriverException;
import com.nikonenko.rideservice.exceptions.WrongPageableParameterException;
import com.nikonenko.rideservice.kafka.producer.UpdateDriverRatingRequestProducer;
import com.nikonenko.rideservice.kafka.producer.UpdatePassengerRatingRequestProducer;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.RideService;
import com.nikonenko.rideservice.services.feign.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.GeodeticCalculator;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final UpdateDriverRatingRequestProducer updateDriverRatingRequestProducer;
    private final UpdatePassengerRatingRequestProducer updatePassengerRatingRequestProducer;
    private final PaymentService paymentService;

    @Override
    public RideResponse getRideById(String rideId) {
        return modelMapper.map(getOrThrow(rideId), RideResponse.class);
    }

    @Override
    public PageResponse<RideResponse> getOpenRides(int pageNumber, int pageSize, String sortField) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortField);
        Page<Ride> page = rideRepository.findAllByStatusIs(RideStatus.OPENED, pageable);
        return getPageRides(page);
    }

    @Override
    public PageResponse<RideResponse> getRidesByPassenger(Long passengerId,
                                                          int pageNumber, int pageSize, String sortField) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortField);
        Page<Ride> page = rideRepository.findAllByPassengerIdIs(passengerId, pageable);
        return getPageRides(page);
    }

    @Override
    public PageResponse<RideResponse> getRidesByDriver(Long driverId,
                                                       int pageNumber, int pageSize, String sortField) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortField);
        Page<Ride> page = rideRepository.findAllByDriverIdIs(driverId, pageable);
        return getPageRides(page);
    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortField) {
        if (pageNumber < 0 || pageSize < 1) {
            throw new WrongPageableParameterException();
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
    }

    private PageResponse<RideResponse> getPageRides(Page<Ride> page) {
        List<RideResponse> rides = page.getContent().stream()
                .map(car -> modelMapper.map(car, RideResponse.class))
                .toList();
        return PageResponse.<RideResponse>builder()
                .objectList(rides)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public CalculateDistanceResponse calculateDistance(LatLng startGeo, LatLng endGeo) {
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        geoCalc.setStartingGeographicPoint(startGeo.lng, startGeo.lat);
        geoCalc.setDestinationGeographicPoint(endGeo.lng, endGeo.lat);

        double distanceInKilometers = geoCalc.getOrthodromicDistance() / 1000;

        return CalculateDistanceResponse.builder()
                .startGeo(startGeo)
                .endGeo(endGeo)
                .distance(distanceInKilometers)
                .build();
    }

    @Override
    public RideResponse createRide(CreateRideRequest createRideRequest) {
        Ride ride = modelMapper.map(createRideRequest, Ride.class);

        if (createRideRequest.getChargeId() != null && !createRideRequest.getChargeId().isEmpty()) {
            checkChargeSuccess(createRideRequest.getChargeId());
            log.info("Charge success");
        }

        ride.setStatus(RideStatus.OPENED);
        ride.setPaymentMethod(getPaymentMethod(createRideRequest));

        Ride savedRide = rideRepository.save(ride);
        log.info("Created ride with id: {}", savedRide.getId());
        return modelMapper.map(savedRide, RideResponse.class);
    }

    private RidePaymentMethod getPaymentMethod(CreateRideRequest createRideRequest) {
        return (createRideRequest.getChargeId() == null || createRideRequest.getChargeId().isEmpty())
                ? RidePaymentMethod.BY_CASH
                : RidePaymentMethod.BY_CARD;
    }

    private void checkChargeSuccess(String chargeId) {
        if (!paymentService.getChargeById(chargeId).isSuccess()) {
            throw new ChargeIsNotSuccessException();
        }
    }

    @Override
    public CloseRideResponse closeRide(String rideId) {
        Ride ride = getOrThrow(rideId);
        if (ride.getStatus() != RideStatus.OPENED) {
            throw new RideIsNotOpenedException();
        }
        CustomerChargeReturnResponse customerChargeReturnResponse = null;
        if (ride.getPaymentMethod() == RidePaymentMethod.BY_CARD) {
            customerChargeReturnResponse = returnCharge(ride);
        }
        rideRepository.delete(ride);
        log.info("Deleted ride with id: {}", rideId);
        return CloseRideResponse.builder()
                .ridePaymentMethod(ride.getPaymentMethod())
                .customerChargeReturnResponse(customerChargeReturnResponse)
                .build();
    }

    public CustomerChargeReturnResponse returnCharge(Ride ride) {
        return paymentService.returnCharge(ride.getChargeId());
    }

    @Override
    public void changeDriverRating(ReviewRequest request) {
        log.info("Request with ride id: {}", request.getRideId());
        Ride ride = getOrThrow(request.getRideId());
        updateDriverRatingRequestProducer.sendRatingDriverRequest(RatingToDriverRequest.builder()
                .driverId(ride.getDriverId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }

    @Override
    public void changePassengerRating(ReviewRequest request) {
        log.info("Request with ride id: {}", request.getRideId());
        Ride ride = getOrThrow(request.getRideId());
        updatePassengerRatingRequestProducer.sendRatingPassengerRequest(RatingToPassengerRequest.builder()
                .passengerId(ride.getPassengerId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }

    @Override
    public void changeRideStatus(ChangeRideStatusRequest request) {
        switch (request.getRideAction()) {
            case ACCEPT -> acceptRide(request);
            case REJECT -> rejectRide(request);
            case START -> startRide(request);
            case FINISH -> finishRide(request);
        }
    }

    public void acceptRide(ChangeRideStatusRequest request) {
        Ride ride = getOrThrow(request.getRideId());
        if (ride.getStatus() != RideStatus.OPENED) {
            throw new RideIsNotOpenedException();
        }
        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ACCEPTED);
        log.info("Accepting ride with id: {}...", ride.getId());
        rideRepository.save(ride);
    }

    public void rejectRide(ChangeRideStatusRequest request) {
        Ride ride = getOrThrow(request.getRideId());
        checkRideAttributes(ride, request.getDriverId(), RideStatus.ACCEPTED, new RideIsNotAcceptedException());
        ride.setDriverId(null);
        ride.setStatus(RideStatus.OPENED);
        log.info("Rejecting ride with id: {}...", ride.getId());
        rideRepository.save(ride);
    }

    public void startRide(ChangeRideStatusRequest request) {
        Ride ride = getOrThrow(request.getRideId());
        checkRideAttributes(ride, request.getDriverId(), RideStatus.ACCEPTED, new RideIsNotAcceptedException());
        ride.setStartDate(LocalDateTime.now());
        ride.setStatus(RideStatus.STARTED);
        log.info("Starting ride with id: {}...", ride.getId());
        rideRepository.save(ride);
    }

    public void finishRide(ChangeRideStatusRequest request) {
        Ride ride = getOrThrow(request.getRideId());
        checkRideAttributes(ride, request.getDriverId(), RideStatus.STARTED, new RideIsNotStartedException());
        ride.setEndDate(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        log.info("Finishing ride with id: {}...", ride.getId());
        rideRepository.save(ride);
    }

    public void checkRideAttributes(Ride ride, Long driverId, RideStatus rideStatus, RuntimeException ex) {
        if (ride.getStatus() != rideStatus) {
            throw ex;
        }
        if (!Objects.equals(ride.getDriverId(), driverId)) {
            throw new UnknownDriverException();
        }
    }

    public Ride getOrThrow(String rideId) {
        return rideRepository.findById(rideId).orElseThrow(RideNotFoundException::new);
    }
}
