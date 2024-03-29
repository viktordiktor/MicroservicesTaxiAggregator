package com.nikonenko.passengerservice.services.impl;

import com.nikonenko.passengerservice.dto.CustomerCreationRequest;
import com.nikonenko.passengerservice.dto.CustomerDataRequest;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingFromPassengerRequest;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.ReviewRequest;
import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CloseRideResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.exceptions.KeycloakUserIsNotValid;
import com.nikonenko.passengerservice.exceptions.NotFoundByPassengerException;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.kafka.producer.CustomerCreationRequestProducer;
import com.nikonenko.passengerservice.kafka.producer.PassengerReviewRequestProducer;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.models.RatingPassenger;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import com.nikonenko.passengerservice.services.PassengerService;
import com.nikonenko.passengerservice.services.feign.PaymentService;
import com.nikonenko.passengerservice.services.feign.RideService;
import com.nikonenko.passengerservice.utils.ExceptionList;
import com.nikonenko.passengerservice.utils.LogList;
import com.nikonenko.passengerservice.utils.PageUtil;
import com.nikonenko.passengerservice.utils.PatternList;
import com.nikonenko.passengerservice.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final PassengerReviewRequestProducer passengerReviewRequestProducer;
    private final CustomerCreationRequestProducer customerCreationRequestProducer;
    private final PaymentService paymentService;
    private final RideService rideService;

    @Override
    public PageResponse<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField) {
        Pageable pageable = PageUtil.createPageable(pageNumber, pageSize, sortField, PassengerResponse.class);
        Page<Passenger> page = passengerRepository.findAll(pageable);
        List<PassengerResponse> passengerResponses = page.getContent().stream()
                .map(passenger -> modelMapper.map(passenger, PassengerResponse.class))
                .toList();
        return PageResponse.<PassengerResponse>builder()
                .objectList(passengerResponses)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public PassengerResponse getPassengerById(UUID id) {
        Passenger passenger = getOrThrow(id);
        log.info(LogList.LOG_GET_PASSENGER, id);
        return modelMapper.map(passenger, PassengerResponse.class);
    }

    @Override
    public PassengerResponse createPassenger(OAuth2User principal) {
        PassengerRequest passengerRequest = createRequestFromPrincipal(principal);
        checkPassengerExists(passengerRequest);
        Passenger passenger = modelMapper.map(passengerRequest, Passenger.class);
        passenger.setId(principal.getAttribute(SecurityUtil.ID));
        Passenger savedPassenger = passengerRepository.save(passenger);
        log.info(LogList.LOG_CREATE_PASSENGER, savedPassenger.getId());
        return modelMapper.map(savedPassenger, PassengerResponse.class);
    }

    private PassengerRequest createRequestFromPrincipal(OAuth2User principal) {
        String phone = principal.getAttribute(SecurityUtil.PHONE);
        String username = principal.getAttribute(SecurityUtil.USERNAME);
        if (phone == null || username == null || !phone.matches(PatternList.PHONE_PATTERN)) {
            throw new KeycloakUserIsNotValid();
        }
        return PassengerRequest.builder()
                .username(username)
                .phone(phone)
                .build();
    }

    @Override
    public RideResponse createRideByPassenger(UUID passengerId, RideByPassengerRequest rideByPassengerRequest) {
        getOrThrow(passengerId);
        CalculateDistanceResponse distanceResponse = getRideDistance(rideByPassengerRequest);

        CustomerCalculateRideResponse calculatePriceResponse = calculateRidePrice(rideByPassengerRequest,
                distanceResponse.getDistance());
        log.info("CalculatePriceResponse: {}", calculatePriceResponse.getPrice());

        CustomerChargeResponse chargeResponse = null;
        if (rideByPassengerRequest.getRidePaymentMethod() == RidePaymentMethod.BY_CARD) {
            checkCustomerExists(passengerId);
            chargeResponse = createCharge(passengerId, calculatePriceResponse.getPrice());
            if (!chargeResponse.isSuccess()) {
                return RideResponse.builder().errorMessage(ExceptionList.CHARGE_IS_NOT_SUCCESS.getValue()).build();
            }
        }

        return createRide(passengerId, rideByPassengerRequest, distanceResponse.getDistance(), chargeResponse);
    }

    private CalculateDistanceResponse getRideDistance(RideByPassengerRequest rideByPassengerRequest) {
        CalculateDistanceRequest distanceRequest = CalculateDistanceRequest.builder()
                .startGeo(rideByPassengerRequest.getStartGeo())
                .endGeo(rideByPassengerRequest.getEndGeo())
                .build();
        CalculateDistanceResponse distanceResponse = rideService.getRideDistance(distanceRequest);
        SecurityUtil.checkException(distanceResponse.getErrorMessage());
        log.info(LogList.LOG_GET_DISTANCE, distanceResponse.getDistance());
        return distanceResponse;
    }

    private CustomerCalculateRideResponse calculateRidePrice(RideByPassengerRequest rideByPassengerRequest,
                                                             double distance) {
        CustomerCalculateRideRequest calculatePriceRequest = CustomerCalculateRideRequest.builder()
                .rideLength(distance)
                .coupon(rideByPassengerRequest.getCoupon())
                .rideDateTime(LocalDateTime.now())
                .build();
        CustomerCalculateRideResponse calculatePriceResponse = paymentService.calculateRidePrice(calculatePriceRequest);
        SecurityUtil.checkException(calculatePriceResponse.getErrorMessage());
        log.info(LogList.LOG_GET_PRICE, calculatePriceResponse.getPrice());
        return calculatePriceResponse;
    }

    private void checkCustomerExists(UUID passengerId) {
        CustomerExistsResponse customerExistsResponse = paymentService.checkCustomerExists(passengerId);
        SecurityUtil.checkException(customerExistsResponse.getErrorMessage());
        if (!paymentService.checkCustomerExists(passengerId).isExists()) {
            throw new NotFoundByPassengerException();
        }
        log.info(LogList.LOG_CUSTOMER_EXISTS);
    }

    private CustomerChargeResponse createCharge(UUID passengerId, BigDecimal amount) {
        CustomerChargeRequest chargeRequest = CustomerChargeRequest.builder()
                .passengerId(passengerId)
                .amount(amount)
                .currency("usd")
                .build();
        CustomerChargeResponse chargeResponse = paymentService.createCharge(chargeRequest);
        SecurityUtil.checkException(chargeResponse.getErrorMessage());
        log.info(LogList.LOG_GET_CHARGE, chargeResponse.getId());
        return chargeResponse;
    }

    private RideResponse createRide(UUID passengerId, RideByPassengerRequest rideByPassengerRequest,
                                    double distance, CustomerChargeResponse chargeResponse) {
        CreateRideRequest createRideRequest = CreateRideRequest.builder()
                .passengerId(passengerId)
                .distance(distance)
                .startAddress(rideByPassengerRequest.getStartAddress())
                .endAddress(rideByPassengerRequest.getEndAddress())
                .chargeId(chargeResponse != null ? chargeResponse.getId() : null)
                .build();
        RideResponse rideResponse = rideService.createRide(createRideRequest);
        SecurityUtil.checkException(rideResponse.getErrorMessage());
        log.info(LogList.LOG_CREATE_RIDE, rideResponse.getId());
        return rideResponse;
    }

    @Override
    public PassengerResponse editPassenger(UUID id, PassengerRequest passengerRequest) {
        checkPassengerExists(passengerRequest);
        Passenger editingPassenger = getOrThrow(id);
        Set<RatingPassenger> passengerRating = editingPassenger.getRatingSet();
        editingPassenger = modelMapper.map(passengerRequest, Passenger.class);
        editingPassenger.setId(id);
        editingPassenger.setRatingSet(passengerRating);
        passengerRepository.save(editingPassenger);
        log.info(LogList.LOG_EDIT_PASSENGER, id);
        return modelMapper.map(editingPassenger, PassengerResponse.class);
    }

    @Override
    public CloseRideResponse closeRide(String rideId) {
        return rideService.closeRide(rideId);
    }

    @Override
    public PageResponse<RideResponse> getPassengerRides(UUID passengerId) {
        return rideService.getRidesByPassengerId(passengerId);
    }

    @Override
    public void deletePassenger(UUID id) {
        passengerRepository.delete(getOrThrow(id));
        log.info(LogList.LOG_DELETE_PASSENGER, id);
    }

    @Override
    public void sendReviewToDriver(String rideId, RatingFromPassengerRequest request) {
        passengerReviewRequestProducer.sendRatingDriverRequest(ReviewRequest.builder()
                .rideId(rideId)
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }

    @Override
    public void createReview(RatingToPassengerRequest request) {
        Passenger passenger = getOrThrow(request.getPassengerId());

        RatingPassenger addingRating = RatingPassenger.builder()
                .passengerId(passenger.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Set<RatingPassenger> modifiedRatingSet = new HashSet<>(passenger.getRatingSet());
        modifiedRatingSet.add(addingRating);

        passenger.setRatingSet(modifiedRatingSet);
        log.info(LogList.LOG_ADD_RATING, request.getPassengerId());
        passengerRepository.save(passenger);
    }

    @Override
    public void createCustomerByPassenger(UUID passengerId, CustomerDataRequest dataRequest) {
        Passenger passenger = getOrThrow(passengerId);
        customerCreationRequestProducer.sendCustomerCreationRequest(CustomerCreationRequest.builder()
                        .passengerId(passengerId)
                        .amount(dataRequest.getAmount())
                        .phone(dataRequest.getPhone() == null ?
                                passenger.getPhone() : dataRequest.getPhone())
                        .username(dataRequest.getUsername() == null ?
                                passenger.getUsername() : dataRequest.getUsername())
                        .build());
    }

    public Passenger getOrThrow(UUID id) {
        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        return optionalPassenger.orElseThrow(PassengerNotFoundException::new);
    }

    public void checkPassengerExists(PassengerRequest passengerRequest) {
        if (passengerRepository.existsByPhone(passengerRequest.getPhone())) {
            throw new PhoneAlreadyExistsException();
        }
        if (passengerRepository.existsByUsername(passengerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
    }
}