package com.nikonenko.rideservice.unit;

import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.ChangeRideStatusRequest;
import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.RatingToDriverRequest;
import com.nikonenko.rideservice.dto.RatingToPassengerRequest;
import com.nikonenko.rideservice.dto.ReviewRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.exceptions.ChargeIsNotSuccessException;
import com.nikonenko.rideservice.exceptions.RideIsNotFinishedException;
import com.nikonenko.rideservice.exceptions.RideIsNotOpenedException;
import com.nikonenko.rideservice.exceptions.RideIsNotStartedException;
import com.nikonenko.rideservice.exceptions.RideNotFoundException;
import com.nikonenko.rideservice.exceptions.WrongPageableParameterException;
import com.nikonenko.rideservice.exceptions.WrongSortFieldException;
import com.nikonenko.rideservice.kafka.producer.UpdateDriverRatingRequestProducer;
import com.nikonenko.rideservice.kafka.producer.UpdatePassengerRatingRequestProducer;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.services.impl.RideServiceImpl;
import com.nikonenko.rideservice.utils.LatLngConverter;
import com.nikonenko.rideservice.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class RideServiceUnitTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UpdatePassengerRatingRequestProducer updatePassengerRatingRequestProducer;
    @Mock
    private UpdateDriverRatingRequestProducer updateDriverRatingRequestProducer;
    @Mock
    private PaymentService paymentService;
    @Mock
    private LatLngConverter latLngConverter;
    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void givenExistsRideId_whenGetRideById_thenReturnRide() {
        RideResponse response = TestUtil.getRideResponse();
        Ride ride = TestUtil.getOpenedByCardRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);
        doReturn(response)
                .when(modelMapper)
                .map(ride, RideResponse.class);

        StepVerifier.create(rideService.getRideById(TestUtil.DEFAULT_RIDE_ID))
                .expectNext(response)
                .verifyComplete();

        verifyNoMoreInteractions(rideRepository);
        verify(modelMapper).map(ride, RideResponse.class);
    }

    @Test
    void givenNonExistsRideId_whenGetRideById_thenThrowException() {
        doReturn(Mono.error(new RideNotFoundException()))
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);

        StepVerifier.create(rideService.getRideById(TestUtil.DEFAULT_RIDE_ID))
                .expectError(RideNotFoundException.class)
                .verify();

        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void givenValidPageParams_whenGetOpenRides_thenReturnPageOpenRides() {
        List<Ride> expectedList = TestUtil.getOpenedRideList();
        List<RideResponse> expectedResponses = TestUtil.getRideResponseList(expectedList, modelMapper);

        doReturn(Flux.fromIterable(expectedList))
                .when(rideRepository)
                .findAllByStatusIs(TestUtil.OPENED_STATUS, PageRequest.of(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        doReturn(TestUtil.getRideResponse())
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));

        StepVerifier.create(rideService.getOpenRides(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT))
                .expectNextCount(expectedResponses.size())
                .expectComplete()
                .verify();

        verify(rideRepository).findAllByStatusIs(TestUtil.OPENED_STATUS, PageRequest.of(TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
    }

    @Test
    void givenInvalidPageParams_whenGetOpenRides_thenThrowException() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> rideService.getOpenRides(TestUtil.INVALID_PAGE,
                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
        );
        verifyNoInteractions(rideRepository);
    }

    @Test
    void givenInvalidSort_whenGetOpenRides_thenThrowException() {
        assertThrows(
                WrongSortFieldException.class,
                () -> rideService.getOpenRides(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.INVALID_PAGE_SORT)
        );
        verifyNoInteractions(rideRepository);
    }

    @Test
    void givenValidPageParams_whenGetPassengerRides_thenReturnPagePassengerRides() {
        List<Ride> expectedList = TestUtil.getOpenedRideList();
        List<RideResponse> expectedResponses = TestUtil.getRideResponseList(expectedList, modelMapper);

        doReturn(Flux.fromIterable(expectedList))
                .when(rideRepository)
                .findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID, PageRequest.of(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
        doReturn(TestUtil.getRideResponse())
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));

        StepVerifier.create(rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT))
                .expectNextCount(expectedResponses.size())
                .expectComplete()
                .verify();

        verify(rideRepository).findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID, PageRequest.of(TestUtil.DEFAULT_PAGE,
                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
    }

    @Test
    void givenInvalidPageParams_whenGetPassengerRides_thenThrowException() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.INVALID_PAGE,
                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
        );
        verifyNoInteractions(rideRepository);
    }

    @Test
    void givenInvalidSort_whenGetPassengerRides_thenThrowException() {
        assertThrows(
                WrongSortFieldException.class,
                () -> rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.INVALID_PAGE_SORT)
        );
        verifyNoInteractions(rideRepository);
    }

    @Test
    void givenStartEndGeo_whenCalculateDistance_thenReturnDistanceResponse() {
        CalculateDistanceResponse response = TestUtil.getCalculateDistanceResponse();

        doReturn(TestUtil.DEFAULT_START_LATLNG)
                .when(latLngConverter)
                .convert(TestUtil.DEFAULT_START_LATLNG.toString());
        doReturn(TestUtil.DEFAULT_END_LATLNG)
                .when(latLngConverter)
                .convert(TestUtil.DEFAULT_END_LATLNG.toString());

        StepVerifier.create(rideService.calculateDistance(TestUtil.DEFAULT_START_LATLNG.toString(),
                        TestUtil.DEFAULT_END_LATLNG.toString()))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void givenRideRequestByCardSuccessful_whenCreateRide_thenReturnRideResponse() {
        RideResponse response = TestUtil.getRideResponse();
        CreateRideRequest request = TestUtil.getCreateRideRequestByCard();
        Ride notSavedRide = TestUtil.getNotSavedRide();
        Ride savedRide = TestUtil.getOpenedByCardRide();
        CustomerChargeResponse chargeResponse = TestUtil.getSuccessfulCustomerChargeResponse();

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(chargeResponse)
                .when(paymentService)
                .getChargeById(TestUtil.DEFAULT_CHARGE_ID);
        doReturn(Mono.just(savedRide))
                .when(rideRepository)
                .save(notSavedRide);
        doReturn(response)
                .when(modelMapper)
                .map(savedRide, RideResponse.class);

        StepVerifier.create(rideService.createRide(request))
                .expectNext(response)
                .verifyComplete();

        verify(modelMapper).map(request, Ride.class);
        verify(paymentService).getChargeById(TestUtil.DEFAULT_CHARGE_ID);
        verify(rideRepository).save(notSavedRide);
        verify(modelMapper).map(savedRide, RideResponse.class);
    }

    @Test
    void givenRideRequestByCash_whenCreateRide_thenReturnRideResponse() {
        RideResponse response = TestUtil.getRideResponse();
        CreateRideRequest request = TestUtil.getCreateRideRequestByCash();
        Ride notSavedRide = TestUtil.getNotSavedRide();
        Ride savedRide = TestUtil.getOpenedByCashRide();

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(Mono.just(savedRide))
                .when(rideRepository)
                .save(notSavedRide);
        doReturn(response)
                .when(modelMapper)
                .map(savedRide, RideResponse.class);

        StepVerifier.create(rideService.createRide(request))
                .expectNext(response)
                .verifyComplete();

        verify(modelMapper).map(request, Ride.class);
        verifyNoInteractions(paymentService);
        verify(rideRepository).save(notSavedRide);
        verify(modelMapper).map(savedRide, RideResponse.class);
    }

    @Test
    void givenRideRequestByCardUnsuccessful_whenCreateRide_thenReturnRideResponse() {
        CreateRideRequest request = TestUtil.getCreateRideRequestByCard();
        Ride notSavedRide = TestUtil.getNotSavedRide();
        CustomerChargeResponse unsuccessfulChargeResponse = TestUtil.getUnsuccessfulCustomerChargeResponse();

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(unsuccessfulChargeResponse)
                .when(paymentService)
                .getChargeById(TestUtil.DEFAULT_CHARGE_ID);

        StepVerifier.create(rideService.createRide(request))
                .expectError(ChargeIsNotSuccessException.class)
                .verify();

        verify(modelMapper).map(request, Ride.class);
        verify(paymentService).getChargeById(TestUtil.DEFAULT_CHARGE_ID);
        verifyNoMoreInteractions(rideRepository);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void givenExistingOpenRideIdByCard_whenCloseRide_thenReturnCloseRideResponse() {
        CloseRideResponse response = TestUtil.getCloseRideByCardResponse();
        CustomerChargeReturnResponse chargeReturnResponse = TestUtil.getCustomerChargeReturnResponse();
        Ride ride = TestUtil.getOpenedByCardRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(ride);
        doReturn(chargeReturnResponse)
                .when(paymentService)
                .returnCharge(ride.getChargeId());

        StepVerifier.create(rideService.closeRide(TestUtil.DEFAULT_RIDE_ID))
                .expectNext(response)
                .verifyComplete();

        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
        verify(paymentService).returnCharge(ride.getChargeId());
        verify(rideRepository).delete(ride);
    }

    @Test
    void givenExistingOpenRideIdByCash_whenCloseRide_thenReturnCloseRideResponse() {
        CloseRideResponse response = TestUtil.getCloseRideByCashResponse();
        Ride ride = TestUtil.getOpenedByCashRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(ride);

        StepVerifier.create(rideService.closeRide(TestUtil.DEFAULT_RIDE_ID))
                .expectNext(response)
                .verifyComplete();

        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
        verify(rideRepository).delete(ride);
        verifyNoInteractions(paymentService);
    }

    @Test
    void givenExistingNotOpenRideId_whenCloseRide_thenReturnCloseRideResponse() {
        Ride ride = TestUtil.getFinishedRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);

        StepVerifier.create(rideService.closeRide(TestUtil.DEFAULT_RIDE_ID))
                .expectError(RideIsNotOpenedException.class)
                .verify();

        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
        verifyNoMoreInteractions(rideRepository);
        verifyNoInteractions(paymentService);
    }

    @Test
    void givenNonExistingRideId_whenCloseRide_thenThrowException() {
        doReturn(Mono.empty())
                .when(rideRepository)
                .findById(TestUtil.DEFAULT_RIDE_ID);

        StepVerifier.create(rideService.closeRide(TestUtil.DEFAULT_RIDE_ID))
                .expectError(RideNotFoundException.class)
                .verify();

        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
        verifyNoMoreInteractions(rideRepository);
        verifyNoInteractions(paymentService);
    }

    @Test
    void givenValidChangeStatusRequest_whenChangeRideStatus_thenChangeRideStatus() {
        ChangeRideStatusRequest request = TestUtil.getAcceptChangeRideStatusRequest();
        Ride ride = TestUtil.getOpenedByCashRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());
        doReturn(Mono.just(ride))
                .when(rideRepository)
                .save(ride);

        StepVerifier.create(rideService.changeRideStatus(request))
                .expectNext()
                .verifyComplete();

        verify(rideRepository).findById(request.getRideId());
        verify(rideRepository).save(ride);
    }

    @Test
    void givenInvalidChangeStatusRequest_whenChangeRideStatus_thenThrowException() {
        ChangeRideStatusRequest request = TestUtil.getFinishChangeRideStatusRequest();
        Ride ride = TestUtil.getOpenedByCashRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changeRideStatus(request))
                .expectError(RideIsNotStartedException.class)
                .verify();

        assertEquals(ride.getStatus(), TestUtil.OPENED_STATUS);
        verify(rideRepository).findById(request.getRideId());
        verifyNoMoreInteractions(rideRepository);
    }

    @Test
    void givenNonExistingChangeStatusRequest_whenChangeRideStatus_thenThrowException() {
        ChangeRideStatusRequest request = TestUtil.getFinishChangeRideStatusRequest();

        doReturn(Mono.empty())
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changeRideStatus(request))
                .expectError(RideNotFoundException.class)
                .verify();

        verify(rideRepository).findById(request.getRideId());
        verifyNoMoreInteractions(rideRepository);
    }

    @Test
    void givenExistingFinishedRideReviewRequest_whenUpdateDriverRating_thenChangeDriverRating() {
        ReviewRequest request = TestUtil.getReviewRequest();
        Ride ride = TestUtil.getFinishedRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changeDriverRating(request))
                .expectNext()
                .verifyComplete();

        verify(rideRepository).findById(request.getRideId());
        verify(updateDriverRatingRequestProducer).sendRatingDriverRequest(any(RatingToDriverRequest.class));
    }

    @Test
    void givenExistingNotFinishedRideReviewRequest_whenUpdateDriverRating_thenThrowException() {
        ReviewRequest request = TestUtil.getReviewRequest();
        Ride ride = TestUtil.getAcceptedRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changeDriverRating(request))
                .expectError(RideIsNotFinishedException.class)
                .verify();

        verify(rideRepository).findById(request.getRideId());
        verifyNoInteractions(updateDriverRatingRequestProducer);
    }

    @Test
    void givenNotExistingRideReviewRequest_whenUpdateDriverRating_thenThrowException() {
        ReviewRequest request = TestUtil.getReviewRequest();

        doReturn(Mono.empty())
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changeDriverRating(request))
                .expectError(RideNotFoundException.class)
                .verify();

        verify(rideRepository).findById(request.getRideId());
        verifyNoInteractions(updateDriverRatingRequestProducer);
    }

    @Test
    void givenExistingFinishedRideReviewRequest_whenUpdatePassengerRating_thenChangePassengerRating() {
        ReviewRequest request = TestUtil.getReviewRequest();
        Ride ride = TestUtil.getFinishedRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changePassengerRating(request))
                .expectNext()
                .verifyComplete();

        verify(rideRepository).findById(request.getRideId());
        verify(updatePassengerRatingRequestProducer).sendRatingPassengerRequest(any(RatingToPassengerRequest.class));
    }

    @Test
    void givenExistingNotFinishedRideReviewRequest_whenUpdatePassengerRating_thenThrowException() {
        ReviewRequest request = TestUtil.getReviewRequest();
        Ride ride = TestUtil.getAcceptedRide();

        doReturn(Mono.just(ride))
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changePassengerRating(request))
                .expectError(RideIsNotFinishedException.class)
                .verify();

        verify(rideRepository).findById(request.getRideId());
        verifyNoInteractions(updatePassengerRatingRequestProducer);
    }

    @Test
    void givenNotExistingRideReviewRequest_whenUpdatePassengerRating_thenThrowException() {
        ReviewRequest request = TestUtil.getReviewRequest();

        doReturn(Mono.empty())
                .when(rideRepository)
                .findById(request.getRideId());

        StepVerifier.create(rideService.changePassengerRating(request))
                .expectError(RideNotFoundException.class)
                .verify();

        verify(rideRepository).findById(request.getRideId());
        verifyNoInteractions(updatePassengerRatingRequestProducer);
    }
}
