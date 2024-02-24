package com.nikonenko.passengerservice.unit;

import com.nikonenko.passengerservice.dto.CustomerDataRequest;
import com.nikonenko.passengerservice.dto.PageResponse;
import com.nikonenko.passengerservice.dto.PassengerRequest;
import com.nikonenko.passengerservice.dto.PassengerResponse;
import com.nikonenko.passengerservice.dto.RatingToPassengerRequest;
import com.nikonenko.passengerservice.dto.RideByPassengerRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerCalculateRideResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeRequest;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerChargeResponse;
import com.nikonenko.passengerservice.dto.feign.payment.CustomerExistsResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceRequest;
import com.nikonenko.passengerservice.dto.feign.ride.CalculateDistanceResponse;
import com.nikonenko.passengerservice.dto.feign.ride.CreateRideRequest;
import com.nikonenko.passengerservice.dto.feign.ride.RideResponse;
import com.nikonenko.passengerservice.exceptions.NotFoundByPassengerException;
import com.nikonenko.passengerservice.exceptions.PassengerNotFoundException;
import com.nikonenko.passengerservice.exceptions.PhoneAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.UsernameAlreadyExistsException;
import com.nikonenko.passengerservice.exceptions.WrongPageableParameterException;
import com.nikonenko.passengerservice.exceptions.WrongSortFieldException;
import com.nikonenko.passengerservice.kafka.producer.CustomerCreationRequestProducer;
import com.nikonenko.passengerservice.models.Passenger;
import com.nikonenko.passengerservice.repositories.PassengerRepository;
import com.nikonenko.passengerservice.services.feign.PaymentService;
import com.nikonenko.passengerservice.services.feign.RideService;
import com.nikonenko.passengerservice.services.impl.PassengerServiceImpl;
import com.nikonenko.passengerservice.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RideService rideService;
    @Mock
    private CustomerCreationRequestProducer customerCreationRequestProducer;
    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void givenValidParams_whenGetPassengers_thenReturnPageResponsePassengerResponse() {
        List<Passenger> expectedList = TestUtil.getPassengerList();
        Page<Passenger> driversPage = new PageImpl<>(expectedList);
        List<PassengerResponse> expectedResponses = TestUtil.getPassengerResponseList(modelMapper, expectedList);

        doReturn(driversPage)
                .when(passengerRepository)
                .findAll(PageRequest.of(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, Sort.by(TestUtil.DEFAULT_PAGE_SORT)));

        PageResponse<PassengerResponse> result =
                passengerService.getAllPassengers(TestUtil.DEFAULT_PAGE,
                        TestUtil.DEFAULT_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT);

        verify(passengerRepository).findAll(any(Pageable.class));
        assertNotNull(result);
        assertEquals(result.getTotalElements(), expectedList.size());
        assertEquals(result.getTotalPages(), driversPage.getTotalPages());
        assertThat(result.getObjectList()).containsExactlyElementsOf(expectedResponses);
    }

    @Test
    void givenInvalidPageParams_whenGetPassengers_thenThrowException() {
        assertThrows(
                WrongPageableParameterException.class,
                () -> passengerService.getAllPassengers(TestUtil.INVALID_PAGE,
                        TestUtil.INVALID_PAGE_SIZE, TestUtil.DEFAULT_PAGE_SORT)
        );
        verifyNoInteractions(passengerRepository);
    }

    @Test
    void givenInvalidSortByParam_whenGetPassengers_thenThrowException() {
        assertThrows(
                WrongSortFieldException.class,
                () -> passengerService.getAllPassengers(TestUtil.DEFAULT_PAGE, TestUtil.DEFAULT_PAGE_SIZE,
                        TestUtil.INVALID_PAGE_SORT)
        );
        verifyNoInteractions(passengerRepository);
    }

    @Test
    void givenNonExistingPassenger_whenCreatePassenger_thenCreateNewPassenger() {
        PassengerResponse response = TestUtil.getDefaultPassengerResponse();
        PassengerRequest request = TestUtil.getDefaultPassengerRequest();
        Passenger notSavedPassenger = TestUtil.getNotSavedPassenger();
        Passenger savedPassenger = TestUtil.getDefaultPassenger();

        doReturn(false)
                .when(passengerRepository)
                .existsByUsername(request.getUsername());
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());
        doReturn(notSavedPassenger)
                .when(modelMapper)
                .map(request, Passenger.class);
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(notSavedPassenger);
        doReturn(response)
                .when(modelMapper)
                .map(savedPassenger, PassengerResponse.class);

        PassengerResponse result = passengerService.createPassenger(request);

        verify(passengerRepository).existsByUsername(request.getUsername());
        verify(passengerRepository).existsByPhone(request.getPhone());
        verify(modelMapper).map(request, Passenger.class);
        verify(passengerRepository).save(notSavedPassenger);
        verify(modelMapper).map(savedPassenger, PassengerResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenPassengerWithExistingPhone_whenCreatePassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getDefaultPassengerRequest();

        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());
        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> passengerService.createPassenger(request)
        );

        verify(passengerRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(passengerRepository);
    }

    @Test
    void givenPassengerWithExistingUsername_whenCreatePassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getDefaultPassengerRequest();

        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(request.getUsername());
        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> passengerService.createPassenger(request)
        );

        verify(passengerRepository).existsByUsername(request.getUsername());
        verify(passengerRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(ignoreStubs(passengerRepository));
    }

    @Test
    void givenExistingPassenger_whenEditPassenger_thenUpdatePassenger() {
        PassengerResponse response = TestUtil.getUpdatePassengerResponse();
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();
        Passenger driver = TestUtil.getDefaultPassenger();
        Passenger editPassenger = TestUtil.getSecondPassenger();

        doReturn(false)
                .when(passengerRepository)
                .existsByUsername(editPassenger.getUsername());
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(editPassenger.getPhone());
        doReturn(Optional.of(driver))
                .when(passengerRepository)
                .findById(TestUtil.DEFAULT_ID);
        doReturn(editPassenger)
                .when(modelMapper)
                .map(request, Passenger.class);
        doReturn(editPassenger)
                .when(passengerRepository)
                .save(editPassenger);
        doReturn(response)
                .when(modelMapper)
                .map(editPassenger, PassengerResponse.class);

        PassengerResponse result = passengerService.editPassenger(TestUtil.DEFAULT_ID, request);

        verify(passengerRepository).existsByUsername(editPassenger.getUsername());
        verify(passengerRepository).existsByPhone(editPassenger.getPhone());
        verify(passengerRepository).findById(TestUtil.DEFAULT_ID);
        verify(modelMapper).map(request, Passenger.class);
        verify(passengerRepository).save(editPassenger);
        verify(modelMapper).map(editPassenger, PassengerResponse.class);
        assertEquals(response, result);
    }

    @Test
    void givenPassengerWithExistingPhone_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();

        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(request.getPhone());

        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> passengerService.editPassenger(TestUtil.DEFAULT_ID, request)
        );

        verify(passengerRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(passengerRepository);
    }

    @Test
    void givenPassengerWithExistingUsername_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();

        doReturn(true)
                .when(passengerRepository)
                .existsByUsername(request.getUsername());

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> passengerService.editPassenger(TestUtil.DEFAULT_ID, request)
        );

        verify(passengerRepository).existsByUsername(request.getUsername());
        verify(passengerRepository).existsByPhone(request.getPhone());
        verifyNoMoreInteractions(ignoreStubs(passengerRepository));
    }

    @Test
    void givenNonExistingPassenger_whenEditPassenger_thenThrowException() {
        PassengerRequest request = TestUtil.getUpdatePassengerRequest();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                PassengerNotFoundException.class,
                () -> passengerService.editPassenger(TestUtil.DEFAULT_ID, request)
        );

        verify(passengerRepository).existsByUsername(request.getUsername());
        verify(passengerRepository).existsByPhone(request.getPhone());
        verify(passengerRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(ignoreStubs(passengerRepository));
    }

    @Test
    void givenExistingPassenger_whenDeletePassenger_thenDeletePassenger() {
        Passenger driver = TestUtil.getDefaultPassenger();

        doReturn(Optional.of(driver))
                .when(passengerRepository)
                .findById(driver.getId());

        passengerService.deletePassenger(driver.getId());

        verify(passengerRepository).findById(driver.getId());
        verify(passengerRepository).delete(driver);
    }

    @Test
    void givenNonExistingPassenger_whenDeletePassenger_thenThrowException() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                PassengerNotFoundException.class,
                () -> passengerService.deletePassenger(TestUtil.DEFAULT_ID)
        );

        verify(passengerRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoMoreInteractions(passengerRepository);
    }

    @Test
    void givenExistingPassenger_whenCreateReview_thenCreateReview() {
        Passenger driver = TestUtil.getDefaultPassenger();
        RatingToPassengerRequest request = TestUtil.getRatingToPassengerRequest();

        doReturn(Optional.of(driver))
                .when(passengerRepository)
                .findById(request.getPassengerId());

        passengerService.createReview(request);

        verify(passengerRepository).findById(request.getPassengerId());
        assertEquals(3, driver.getRatingSet().size());
    }

    @Test
    void givenNonExistingPassenger_whenCreateReview_thenThrowException() {
        RatingToPassengerRequest request = TestUtil.getRatingToPassengerRequest();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(request.getPassengerId());

        assertThrows(
                PassengerNotFoundException.class,
                () -> passengerService.createReview(request)
        );

        verify(passengerRepository).findById(request.getPassengerId());
        verifyNoMoreInteractions(passengerRepository);
    }

    @Test
    void givenExistingPassenger_whenCreateCustomer_thenCreateCustomer() {
        Passenger passenger = TestUtil.getDefaultPassenger();
        CustomerDataRequest request = TestUtil.getCustomerDataRequest();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(passenger.getId());

        passengerService.createCustomerByPassenger(passenger.getId(), request);

        verify(passengerRepository).findById(passenger.getId());
        verify(customerCreationRequestProducer).sendCustomerCreationRequest(TestUtil.getCustomerCreationRequest());
    }

    @Test
    void givenNonExistingPassenger_whenCreateCustomer_thenThrowException() {
        CustomerDataRequest request = TestUtil.getCustomerDataRequest();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(TestUtil.DEFAULT_ID);

        assertThrows(
                PassengerNotFoundException.class,
                () -> passengerService.createCustomerByPassenger(TestUtil.DEFAULT_ID, request)
        );

        verify(passengerRepository).findById(TestUtil.DEFAULT_ID);
        verifyNoInteractions(customerCreationRequestProducer);
    }

    @Test
    void givenExistingPassengerAndCustomer_whenCreateRideByCard_thenCreateRideByCard() {
        RideByPassengerRequest request = TestUtil.getRideByPassengerCardRequest();
        Passenger passenger = TestUtil.getDefaultPassenger();
        CalculateDistanceRequest distanceRequest = TestUtil.getCalculateDistanceRequest();
        CalculateDistanceResponse distanceResponse = TestUtil.getCalculateDistanceResponse();
        CustomerCalculateRideResponse calculateRideResponse = TestUtil.getCustomerCalculateRideResponse();
        CustomerExistsResponse existsResponse = TestUtil.getCustomerExistsResponse();
        CustomerChargeRequest chargeRequest = TestUtil.getCustomerChargeRequest();
        CustomerChargeResponse chargeResponse = TestUtil.getCustomerChargeResponse();
        CreateRideRequest rideRequest = TestUtil.getCreateRideByCardRequest();
        RideResponse rideResponse = TestUtil.getRideResponseByCard();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(passenger.getId());
        doReturn(distanceResponse)
                .when(rideService)
                .getRideDistance(distanceRequest);
        doReturn(calculateRideResponse)
                .when(paymentService)
                .calculateRidePrice(any(CustomerCalculateRideRequest.class));
        doReturn(existsResponse)
                .when(paymentService)
                .checkCustomerExists(passenger.getId());
        doReturn(chargeResponse)
                .when(paymentService)
                .createCharge(chargeRequest);
        doReturn(rideResponse)
                .when(rideService)
                .createRide(rideRequest);

        passengerService.createRideByPassenger(passenger.getId(), request);

        verify(passengerRepository).findById(passenger.getId());
        verify(rideService).getRideDistance(distanceRequest);
        verify(paymentService).calculateRidePrice(any(CustomerCalculateRideRequest.class));
        verify(paymentService).checkCustomerExists(passenger.getId());
        verify(paymentService).createCharge(chargeRequest);
        verify(rideService).createRide(rideRequest);
    }

    @Test
    void givenExistingPassenger_whenCreateRideByCash_thenCreateRideByCash() {
        RideByPassengerRequest request = TestUtil.getRideByPassengerCashRequest();
        Passenger passenger = TestUtil.getDefaultPassenger();
        CalculateDistanceRequest distanceRequest = TestUtil.getCalculateDistanceRequest();
        CalculateDistanceResponse distanceResponse = TestUtil.getCalculateDistanceResponse();
        CustomerCalculateRideResponse calculateRideResponse = TestUtil.getCustomerCalculateRideResponse();
        CreateRideRequest rideRequest = TestUtil.getCreateRideByCashRequest();
        RideResponse rideResponse = TestUtil.getRideResponseByCash();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(passenger.getId());
        doReturn(distanceResponse)
                .when(rideService)
                .getRideDistance(distanceRequest);
        doReturn(calculateRideResponse)
                .when(paymentService)
                .calculateRidePrice(any(CustomerCalculateRideRequest.class));
        doReturn(rideResponse)
                .when(rideService)
                .createRide(rideRequest);

        passengerService.createRideByPassenger(passenger.getId(), request);

        verify(passengerRepository).findById(passenger.getId());
        verify(rideService).getRideDistance(distanceRequest);
        verify(paymentService).calculateRidePrice(any(CustomerCalculateRideRequest.class));
        verifyNoMoreInteractions(paymentService);
        verify(rideService).createRide(rideRequest);
    }

    @Test
    void givenExistingPassengerAndNonExistingCustomer_whenCreateRideByCard_thenThrowException() {
        RideByPassengerRequest request = TestUtil.getRideByPassengerCardRequest();
        Passenger passenger = TestUtil.getDefaultPassenger();
        CalculateDistanceRequest distanceRequest = TestUtil.getCalculateDistanceRequest();
        CalculateDistanceResponse distanceResponse = TestUtil.getCalculateDistanceResponse();
        CustomerCalculateRideResponse calculateRideResponse = TestUtil.getCustomerCalculateRideResponse();
        CustomerExistsResponse nonExistsResponse = TestUtil.getNotExistingCustomerExistsResponse();

        doReturn(Optional.of(passenger))
                .when(passengerRepository)
                .findById(passenger.getId());
        doReturn(distanceResponse)
                .when(rideService)
                .getRideDistance(distanceRequest);
        doReturn(calculateRideResponse)
                .when(paymentService)
                .calculateRidePrice(any(CustomerCalculateRideRequest.class));
        doReturn(nonExistsResponse)
                .when(paymentService)
                .checkCustomerExists(passenger.getId());

        assertThrows(
                NotFoundByPassengerException.class,
                () -> passengerService.createRideByPassenger(passenger.getId(), request)
        );

        verify(passengerRepository).findById(passenger.getId());
        verify(rideService).getRideDistance(distanceRequest);
        verify(paymentService).calculateRidePrice(any(CustomerCalculateRideRequest.class));
        verify(paymentService).checkCustomerExists(passenger.getId());
        verifyNoMoreInteractions(paymentService);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void givenNonExistingPassenger_whenCreateRide_thenThrowException() {
        RideByPassengerRequest request = TestUtil.getRideByPassengerCardRequest();
        Passenger passenger = TestUtil.getDefaultPassenger();

        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(passenger.getId());

        assertThrows(
                PassengerNotFoundException.class,
                () -> passengerService.createRideByPassenger(passenger.getId(), request)
        );

        verify(passengerRepository).findById(passenger.getId());
        verifyNoMoreInteractions(passengerRepository);
        verifyNoMoreInteractions(paymentService);
        verifyNoMoreInteractions(rideService);
    }
}
