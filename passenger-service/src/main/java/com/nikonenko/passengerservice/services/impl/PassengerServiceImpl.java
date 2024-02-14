package com.nikonenko.passengerservice.services.impl;

import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingFromPassengerRequest;
import com.nikonenko.passengerservice.dto.ReviewRequest;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.exceptions.NotFoundByPassengerException;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.WrongPageableParameterException;
import com.nikonenko.passengerservice.kafka.producer.PassengerReviewRequestProducer;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.models.RatingPassenger;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import com.nikonenko.passengerservice.services.PassengerService;
import com.nikonenko.passengerservice.services.feign.PaymentService;
import com.nikonenko.passengerservice.services.feign.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final PassengerReviewRequestProducer passengerReviewRequestProducer;
    private final PaymentService paymentService;
    private final RideService rideService;

    @Override
    public PageResponse<PassengerResponse> getAllPassengers(int pageNumber, int pageSize, String sortField) {
        if (pageNumber < 0 || pageSize < 1) {
            throw new WrongPageableParameterException();
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
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
    public PassengerResponse getPassengerById(Long id) {
        return modelMapper.map(getPassenger(id), PassengerResponse.class);
    }

    @Override
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        checkPassengerExists(passengerRequest);
        Passenger passenger = modelMapper.map(passengerRequest, Passenger.class);
        Passenger savedPassenger = passengerRepository.save(passenger);
        log.info("Passenger created with id: {}", savedPassenger.getId());
        return modelMapper.map(savedPassenger, PassengerResponse.class);
    }

    @Override
    public RideResponse createRideByPassenger(Long passengerId, RideByPassengerRequest rideByPassengerRequest) {
        log.info("Request for distance..");
        CalculateDistanceResponse distanceResponse = rideService.getRideDistance(CalculateDistanceRequest.builder()
                        .startGeo(rideByPassengerRequest.getStartGeo())
                        .endGeo(rideByPassengerRequest.getEndGeo())
                        .build());
        log.info("Got distance");

        log.info("\nRequest for price...");
        CustomerCalculateRideResponse calculatePriceResponse =
                paymentService.calculateRidePrice(CustomerCalculateRideRequest.builder()
                        .rideLength(distanceResponse.getDistance())
                        .coupon(rideByPassengerRequest.getCoupon())
                        .rideDateTime(LocalDateTime.now())
                        .build());
        log.info("Got price: {}", calculatePriceResponse.getPrice());

        CustomerChargeResponse chargeResponse = null;
        if (rideByPassengerRequest.getRidePaymentMethod() == RidePaymentMethod.BY_CARD) {
            log.info("\nRequest for check customer exists..");
            if (!paymentService.checkCustomerExists(passengerId).isExists()) {
                throw new NotFoundByPassengerException();
            }
            log.info("Customer exists");

            log.info("\n Request for Charge...");
            chargeResponse = paymentService.createCharge(CustomerChargeRequest.builder()
                            .passengerId(passengerId)
                            .amount(calculatePriceResponse.getPrice())
                            .currency(rideByPassengerRequest.getCurrency())
                            .build());
            log.info("Got Charge: {}", chargeResponse.getId());
        }
        log.info("\n Request for Ride...");
        return rideService.createRide(CreateRideRequest.builder()
                        .passengerId(passengerId)
                        .distance(distanceResponse.getDistance())
                        .startAddress(rideByPassengerRequest.getStartAddress())
                        .endAddress(rideByPassengerRequest.getEndAddress())
                        .chargeId(chargeResponse != null ? chargeResponse.getId() : null)
                        .build());
    }

    @Override
    public PassengerResponse editPassenger(Long id, PassengerRequest passengerRequest) {
        checkPassengerExists(passengerRequest);
        Passenger editingPassenger = getPassenger(id);
        modelMapper.map(passengerRequest, editingPassenger);
        passengerRepository.save(editingPassenger);
        log.info("Passenger edited with id: {}", id);
        return modelMapper.map(editingPassenger, PassengerResponse.class);
    }

    @Override
    public void deletePassenger(Long id) {
        passengerRepository.delete(getPassenger(id));
        log.info("Passenger deleted with id: {}", id);
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
        Passenger passenger = getPassenger(request.getPassengerId());

        RatingPassenger addingRating = RatingPassenger.builder()
                .passengerId(passenger.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Set<RatingPassenger> modifiedRatingSet = passenger.getRatingSet();
        modifiedRatingSet.add(addingRating);

        passenger.setRatingSet(modifiedRatingSet);
        log.info("Review added to passenger with id: {}", request.getPassengerId());
        passengerRepository.save(passenger);
    }

    public Passenger getPassenger(Long id) {
        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        return optionalPassenger.orElseThrow(PassengerNotFoundException::new);
    }

    public void checkPassengerExists(PassengerRequest passengerRequest) {
        if (passengerRepository.existsByPhone(passengerRequest.getPhone())) {
            log.info("Passenger with phone {} already exists!", passengerRequest.getPhone());
            throw new PhoneAlreadyExistsException();
        }
        if (passengerRepository.existsByUsername(passengerRequest.getUsername())) {
            log.info("Passenger with username {} already exists!", passengerRequest.getUsername());
            throw new UsernameAlreadyExistsException();
        }
    }
}