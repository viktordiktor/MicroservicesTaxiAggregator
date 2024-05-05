package com.nikonenko.rideservice.unit;

import com.nikonenko.rideservice.kafka.producer.UpdateDriverRatingRequestProducer;
import com.nikonenko.rideservice.kafka.producer.UpdatePassengerRatingRequestProducer;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.services.impl.RideServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    @InjectMocks
    private RideServiceImpl rideService;

    //TODO To Mono
//    @Test
//    void givenExistsRideId_whenGetRideById_thenReturnRide() {
//        RideResponse response = TestUtil.getRideResponse();
//        Ride ride = TestUtil.getOpenedByCardRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//        doReturn(response)
//                .when(modelMapper)
//                .map(ride, RideResponse.class);
//
//        RideResponse result = rideService.getRideById(TestUtil.DEFAULT_RIDE_ID);
//
//        assertEquals(response, result);
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verify(modelMapper).map(ride, RideResponse.class);
//    }

//    @Test
//    void givenNonExistsRideId_whenGetRideById_thenThrowException() {
//        doReturn(Optional.empty())
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//
//        assertThrows(
//                RideNotFoundException.class,
//                () -> rideService.getRideById(TestUtil.DEFAULT_RIDE_ID)
//        );
//
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verifyNoInteractions(modelMapper);
//    }
//
//    @Test
//    void givenValidPageParams_whenGetOpenRides_thenReturnPageOpenRides() {
//        List<Ride> expectedList = TestUtil.getOpenedRideList();
//        Page<Ride> ridesPage = new PageImpl<>(expectedList);
//        List<RideResponse> expectedResponses = TestUtil.getRideResponseList(expectedList, modelMapper);
//
//        doReturn(ridesPage)
//                .when(rideRepository)
//                .findAllByStatusIs(TestUtil.OPENED_STATUS, PageRequest.of(TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
//
//        PageResponse<RideResponse> result =
//                rideService.getOpenRides(TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);
//
//        verify(rideRepository).findAllByStatusIs(TestUtil.OPENED_STATUS, PageRequest.of(TestUtil.DEFAULT_PAGE,
//                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
//        assertNotNull(result);
//        assertEquals(result.getTotalElements(), expectedList.size());
//        assertEquals(result.getTotalPages(), ridesPage.getTotalPages());
//        assertThat(result.getObjectList()).containsExactlyElementsOf(expectedResponses);
//    }
//
//    @Test
//    void givenInvalidPageParams_whenGetOpenRides_thenThrowException() {
//        assertThrows(
//                WrongPageableParameterException.class,
//                () -> rideService.getOpenRides(TestUtil.INVALID_PAGE,
//                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
//        );
//        verifyNoInteractions(rideRepository);
//    }
//
//    @Test
//    void givenInvalidSort_whenGetOpenRides_thenThrowException() {
//        assertThrows(
//                WrongSortFieldException.class,
//                () -> rideService.getOpenRides(TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.INVALID_PAGE_SORT)
//        );
//        verifyNoInteractions(rideRepository);
//    }
//
//    @Test
//    void givenValidPageParams_whenGetPassengerRides_thenReturnPagePassengerRides() {
//        List<Ride> expectedList = TestUtil.getRideList();
//        Page<Ride> ridesPage = new PageImpl<>(expectedList);
//        List<RideResponse> expectedResponses = TestUtil.getRideResponseList(expectedList, modelMapper);
//
//        doReturn(ridesPage)
//                .when(rideRepository)
//                .findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID, PageRequest.of(TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
//
//        PageResponse<RideResponse> result =
//                rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);
//
//        verify(rideRepository).findAllByPassengerIdIs(TestUtil.DEFAULT_PASSENGER_ID, PageRequest.of(TestUtil.DEFAULT_PAGE,
//                TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));
//        assertNotNull(result);
//        assertEquals(result.getTotalElements(), expectedList.size());
//        assertEquals(result.getTotalPages(), ridesPage.getTotalPages());
//        assertThat(result.getObjectList()).containsExactlyElementsOf(expectedResponses);
//    }
//
//    @Test
//    void givenInvalidPageParams_whenGetPassengerRides_thenThrowException() {
//        assertThrows(
//                WrongPageableParameterException.class,
//                () -> rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.INVALID_PAGE,
//                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
//        );
//        verifyNoInteractions(rideRepository);
//    }
//
//    @Test
//    void givenInvalidSort_whenGetPassengerRides_thenThrowException() {
//        assertThrows(
//                WrongSortFieldException.class,
//                () -> rideService.getRidesByPassenger(TestUtil.DEFAULT_PASSENGER_ID, TestUtil.DEFAULT_PAGE,
//                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.INVALID_PAGE_SORT)
//        );
//        verifyNoInteractions(rideRepository);
//    }

    //TODO To Mono
//    @Test
//    void givenStartEndGeo_whenCalculateDistance_thenReturnDistanceResponse() {
//        CalculateDistanceResponse response = TestUtil.getCalculateDistanceResponse();
//
//        CalculateDistanceResponse result = rideService.calculateDistance(TestUtil.DEFAULT_START_LATLNG, TestUtil.DEFAULT_END_LATLNG);
//
//        assertEquals(response, result);
//    }

    //TODO To Mono
//    @Test
//    void givenRideRequestByCardSuccessful_whenCreateRide_thenReturnRideResponse() {
//        RideResponse response = TestUtil.getRideResponse();
//        CreateRideRequest request = TestUtil.getCreateRideRequestByCard();
//        Ride notSavedRide = TestUtil.getNotSavedRide();
//        Ride savedRide = TestUtil.getOpenedByCardRide();
//        CustomerChargeResponse chargeResponse = TestUtil.getSuccessfulCustomerChargeResponse();
//
//        doReturn(notSavedRide)
//                .when(modelMapper)
//                .map(request, Ride.class);
//        doReturn(chargeResponse)
//                .when(paymentService)
//                .getChargeById(TestUtil.DEFAULT_CHARGE_ID);
//        doReturn(savedRide)
//                .when(rideRepository)
//                .save(notSavedRide);
//        doReturn(response)
//                .when(modelMapper)
//                .map(savedRide, RideResponse.class);
//
//        RideResponse result = rideService.createRide(request);
//
//        assertEquals(response, result);
//        verify(modelMapper).map(request, Ride.class);
//        verify(paymentService).getChargeById(TestUtil.DEFAULT_CHARGE_ID);
//        verify(rideRepository).save(notSavedRide);
//        verify(modelMapper).map(savedRide, RideResponse.class);
//    }

    //TODO To Mono
//    @Test
//    void givenRideRequestByCash_whenCreateRide_thenReturnRideResponse() {
//        RideResponse response = TestUtil.getRideResponse();
//        CreateRideRequest request = TestUtil.getCreateRideRequestByCash();
//        Ride notSavedRide = TestUtil.getNotSavedRide();
//        Ride savedRide = TestUtil.getOpenedByCashRide();
//
//        doReturn(notSavedRide)
//                .when(modelMapper)
//                .map(request, Ride.class);
//        doReturn(savedRide)
//                .when(rideRepository)
//                .save(notSavedRide);
//        doReturn(response)
//                .when(modelMapper)
//                .map(savedRide, RideResponse.class);
//
//        RideResponse result = rideService.createRide(request);
//
//        assertEquals(response, result);
//        verify(modelMapper).map(request, Ride.class);
//        verifyNoInteractions(paymentService);
//        verify(rideRepository).save(notSavedRide);
//        verify(modelMapper).map(savedRide, RideResponse.class);
//    }

//    @Test
//    void givenRideRequestByCardUnsuccessful_whenCreateRide_thenReturnRideResponse() {
//        CreateRideRequest request = TestUtil.getCreateRideRequestByCard();
//        Ride notSavedRide = TestUtil.getNotSavedRide();
//        CustomerChargeResponse unsuccessfulChargeResponse = TestUtil.getUnsuccessfulCustomerChargeResponse();
//
//        doReturn(notSavedRide)
//                .when(modelMapper)
//                .map(request, Ride.class);
//        doReturn(unsuccessfulChargeResponse)
//                .when(paymentService)
//                .getChargeById(TestUtil.DEFAULT_CHARGE_ID);
//
//        assertThrows(
//                ChargeIsNotSuccessException.class,
//                () -> rideService.createRide(request)
//        );
//
//        verify(modelMapper).map(request, Ride.class);
//        verify(paymentService).getChargeById(TestUtil.DEFAULT_CHARGE_ID);
//        verifyNoMoreInteractions(rideRepository);
//        verifyNoMoreInteractions(modelMapper);
//    }
//
//    @Test
//    void givenExistingOpenRideIdByCard_whenCloseRide_thenReturnCloseRideResponse() {
//        CloseRideResponse response = TestUtil.getCloseRideByCardResponse();
//        CustomerChargeReturnResponse chargeReturnResponse = TestUtil.getCustomerChargeReturnResponse();
//        Ride ride = TestUtil.getOpenedByCardRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//        doReturn(chargeReturnResponse)
//                .when(paymentService)
//                .returnCharge(ride.getChargeId());
//
//        CloseRideResponse result = rideService.closeRide(TestUtil.DEFAULT_RIDE_ID);
//
//        assertEquals(response, result);
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verify(paymentService).returnCharge(ride.getChargeId());
//        verify(rideRepository).delete(ride);
//    }
//
//    @Test
//    void givenExistingOpenRideIdByCash_whenCloseRide_thenReturnCloseRideResponse() {
//        CloseRideResponse response = TestUtil.getCloseRideByCashResponse();
//        Ride ride = TestUtil.getOpenedByCashRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//
//        CloseRideResponse result = rideService.closeRide(TestUtil.DEFAULT_RIDE_ID);
//
//        assertEquals(response, result);
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verify(rideRepository).delete(ride);
//        verifyNoInteractions(paymentService);
//    }
//
//    @Test
//    void givenExistingNotOpenRideId_whenCloseRide_thenReturnCloseRideResponse() {
//        Ride ride = TestUtil.getFinishedRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//
//        assertThrows(
//                RideIsNotOpenedException.class,
//                () -> rideService.closeRide(TestUtil.DEFAULT_RIDE_ID)
//        );
//
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verifyNoMoreInteractions(rideRepository);
//        verifyNoInteractions(paymentService);
//    }
//
//    @Test
//    void givenNonExistingRideId_whenCloseRide_thenThrowException() {
//        doReturn(Optional.empty())
//                .when(rideRepository)
//                .findById(TestUtil.DEFAULT_RIDE_ID);
//
//        assertThrows(
//                RideNotFoundException.class,
//                () -> rideService.closeRide(TestUtil.DEFAULT_RIDE_ID)
//        );
//
//        verify(rideRepository).findById(TestUtil.DEFAULT_RIDE_ID);
//        verifyNoMoreInteractions(rideRepository);
//        verifyNoInteractions(paymentService);
//    }
//
//    @Test
//    void givenValidChangeStatusRequest_whenChangeRideStatus_thenChangeRideStatus() {
//        ChangeRideStatusRequest request = TestUtil.getAcceptChangeRideStatusRequest();
//        Ride ride = TestUtil.getOpenedByCashRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        rideService.changeRideStatus(request);
//
//        assertEquals(ride.getStatus(), TestUtil.ACCEPTED_STATUS);
//        verify(rideRepository).findById(request.getRideId());
//        verify(rideRepository).save(ride);
//    }
//
//    @Test
//    void givenInvalidChangeStatusRequest_whenChangeRideStatus_thenThrowException() {
//        ChangeRideStatusRequest request = TestUtil.getFinishChangeRideStatusRequest();
//        Ride ride = TestUtil.getOpenedByCashRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideIsNotStartedException.class,
//                () -> rideService.changeRideStatus(request)
//        );
//
//        assertEquals(ride.getStatus(), TestUtil.OPENED_STATUS);
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoMoreInteractions(rideRepository);
//    }
//
//    @Test
//    void givenNonExistingChangeStatusRequest_whenChangeRideStatus_thenThrowException() {
//        ChangeRideStatusRequest request = TestUtil.getFinishChangeRideStatusRequest();
//
//        doReturn(Optional.empty())
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideNotFoundException.class,
//                () -> rideService.changeRideStatus(request)
//        );
//
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoMoreInteractions(rideRepository);
//    }
//
//    @Test
//    void givenExistingFinishedRideReviewRequest_whenUpdateDriverRating_thenChangeDriverRating() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//        Ride ride = TestUtil.getFinishedRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        rideService.changeDriverRating(request);
//
//        verify(rideRepository).findById(request.getRideId());
//        verify(updateDriverRatingRequestProducer).sendRatingDriverRequest(any(RatingToDriverRequest.class));
//    }
//
//    @Test
//    void givenExistingNotFinishedRideReviewRequest_whenUpdateDriverRating_thenThrowException() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//        Ride ride = TestUtil.getAcceptedRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideIsNotFinishedException.class,
//                () -> rideService.changeDriverRating(request)
//        );
//
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoInteractions(updateDriverRatingRequestProducer);
//    }
//
//    @Test
//    void givenNotExistingRideReviewRequest_whenUpdateDriverRating_thenThrowException() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//
//        doReturn(Optional.empty())
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideNotFoundException.class,
//                () -> rideService.changeDriverRating(request)
//        );
//
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoInteractions(updateDriverRatingRequestProducer);
//    }
//
//    @Test
//    void givenExistingFinishedRideReviewRequest_whenUpdatePassengerRating_thenChangePassengerRating() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//        Ride ride = TestUtil.getFinishedRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        rideService.changePassengerRating(request);
//
//        verify(rideRepository).findById(request.getRideId());
//        verify(updatePassengerRatingRequestProducer).sendRatingPassengerRequest(any(RatingToPassengerRequest.class));
//    }
//
//    @Test
//    void givenExistingNotFinishedRideReviewRequest_whenUpdatePassengerRating_thenThrowException() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//        Ride ride = TestUtil.getAcceptedRide();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideIsNotFinishedException.class,
//                () -> rideService.changePassengerRating(request)
//        );
//
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoInteractions(updatePassengerRatingRequestProducer);
//    }
//
//    @Test
//    void givenNotExistingRideReviewRequest_whenUpdatePassengerRating_thenThrowException() {
//        ReviewRequest request = TestUtil.getReviewRequest();
//
//        doReturn(Optional.empty())
//                .when(rideRepository)
//                .findById(request.getRideId());
//
//        assertThrows(
//                RideNotFoundException.class,
//                () -> rideService.changePassengerRating(request)
//        );
//
//        verify(rideRepository).findById(request.getRideId());
//        verifyNoInteractions(updatePassengerRatingRequestProducer);
//    }
}
