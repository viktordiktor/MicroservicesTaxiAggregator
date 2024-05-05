package com.nikonenko.rideservice.component;

import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.services.impl.RideServiceImpl;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

@CucumberContextConfiguration
public class RideStepDefinitions {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private RideServiceImpl rideService;
    private RideResponse actualResponse;
    private CloseRideResponse actualCloseRideResponse;
    private Ride actualRide;
    private RuntimeException exception;

    //TODO To Mono
//    @Given("Ride with ID {string} exists")
//    public void rideWithIdExists(String id) {
//        Ride ride = TestUtil.getFinishedRide();
//        RideResponse expectedResponse = TestUtil.getRideResponse();
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(id);
//        doReturn(expectedResponse)
//                .when(modelMapper)
//                .map(ride, RideResponse.class);
//
//        RideResponse result = rideService.getRideById(id);
//        assertEquals(expectedResponse, result);
//    }
//
//    @When("getRideById method is called with ID {string}")
//    public void findByIdMethodIsCalledWithId(String id) {
//        try {
//            actualResponse = rideService.getRideById(id);
//        } catch (RuntimeException ex) {
//            exception = ex;
//        }
//    }

//    @Then("RideResponse should contains Ride with ID {string}")
//    public void rideResponseShouldContainsRideWithId(String id) {
//        Ride ride = rideRepository.findById(id).get();
//        RideResponse expected = modelMapper.map(ride, RideResponse.class);
//
//        assertNull(exception);
//        assertEquals(expected, actualResponse);
//    }
//
//    @Given("Ride with ID {string} not exists")
//    public void rideWithIdNotExists(String id) {
//        assertTrue(rideRepository.findById(id).isEmpty());
//    }
//
//    @Then("RideNotFoundException should be thrown for Ride ID {string}")
//    public void rideNotFoundExceptionThrownForId(String id) {
//        RideNotFoundException expected = new RideNotFoundException();
//
//        assertEquals(exception.getMessage(), expected.getMessage());
//    }
//
//    @Given("CreateRideRequest by card of Passenger ID {string} and Start Address {string} and End Address {string} and success Charge ID {string}")
//    public void createRideRequestByCardOfPassengerIDAndStartAddressAndEndAddressAndSuccessChargeID
//            (String passengerId, String startAddress, String endAddress, String chargeId) {
//        RideResponse response = TestUtil
//                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        CreateRideRequest request = TestUtil
//                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        Ride notSavedRide = TestUtil
//                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        Ride savedRide = TestUtil
//                .getOpenedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        CustomerChargeResponse chargeResponse = TestUtil.getSuccessfulCustomerChargeResponse();
//
//        doReturn(notSavedRide)
//                .when(modelMapper)
//                .map(request, Ride.class);
//        doReturn(chargeResponse)
//                .when(paymentService)
//                .getChargeById(chargeId);
//        doReturn(savedRide)
//                .when(rideRepository)
//                .save(notSavedRide);
//        doReturn(response)
//                .when(modelMapper)
//                .map(savedRide, RideResponse.class);
//    }

    //TODO to Mono
//    @When("createRide method is called with Passenger ID {string} and Start Address {string} and End Address {string} and Charge ID {string}")
//    public void createRideMethodIsCalledWithPassengerIdAndStartAddressAndEndAddressAndChargeId
//            (String passengerId, String startAddress, String endAddress, String chargeId) {
//        try {
//            actualResponse = rideService.createRide(TestUtil
//                    .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId));
//        } catch (RuntimeException ex) {
//            exception = ex;
//        }
//    }

//    @Then("RideResponse should contains Passenger ID {string} and Start Address {string} and End Address {string} and Charge ID {string}")
//    public void rideResponseShouldContainsPassengerIdAndStartAddressAndEndAddressAndChargeId
//            (String passengerId, String startAddress, String endAddress, String chargeId) {
//        RideResponse expected = TestUtil
//                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//
//        assertNull(exception);
//        assertEquals(expected, actualResponse);
//    }
//
//    @Given("CreateRideRequest by card of Passenger ID {string} and Start Address {string} and End Address {string} and unsuccessful Charge ID {string}")
//    public void createRideRequestByCardOfPassengerIDAndStartAddressAndEndAddressAndUnsuccessfulChargeID
//            (String passengerId, String startAddress, String endAddress, String chargeId) {
//        CreateRideRequest request = TestUtil
//                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        Ride notSavedRide = TestUtil
//                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
//        CustomerChargeResponse chargeResponse = TestUtil.getUnsuccessfulCustomerChargeResponse();
//
//        doReturn(notSavedRide)
//                .when(modelMapper)
//                .map(request, Ride.class);
//        doReturn(chargeResponse)
//                .when(paymentService)
//                .getChargeById(chargeId);
//    }
//
//    @Then("ChargeIsNotSuccessException should be thrown for Charge ID {string}")
//    public void chargeIsNotSuccessThrownForId(String chargeId) {
//        ChargeIsNotSuccessException expected = new ChargeIsNotSuccessException();
//
//        assertEquals(exception.getMessage(), expected.getMessage());
//    }
//
//    @Given("CreateRideRequest by cash of Passenger ID {string} and Start Address {string} and End Address {string}")
//    public void createRideRequestByCashOfPassengerIDAndStartAddressAndEndAddress(String passengerId, String startAddress,
//                                                                                 String endAddress) {
//        RideResponse response = TestUtil
//                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
//        CreateRideRequest request = TestUtil
//                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
//        Ride notSavedRide = TestUtil
//                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
//        Ride savedRide = TestUtil
//                .getOpenedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
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
//    }

    //TODO To Mono
//    @When("createRide method is called with Passenger ID {stromg} and Start Address {string} and End Address {string}")
//    public void createRideMethodIsCalledWithPassengerIdAndStartAddressAndEndAddressAndChargeId
//            (String passengerId, String startAddress, String endAddress) {
//        try {
//            actualResponse = rideService.createRide(TestUtil.getCreateRideRequestWithParameters(
//                    UUID.fromString(passengerId), startAddress, endAddress, null));
//        } catch (RuntimeException ex) {
//            exception = ex;
//        }
//    }

//    @Then("RideResponse should contains Passenger ID {string} and Start Address {string} and End Address {string}")
//    public void rideResponseShouldContainsPassengerIdAndStartAddressAndEndAddress(String passengerId, String startAddress,
//                                                                                  String endAddress) {
//        RideResponse expected = TestUtil
//                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
//
//        assertNull(exception);
//        assertEquals(expected, actualResponse);
//    }
//
//    @Given("Ride with ID {string} exists and opened by card")
//    public void rideWithIdExistsAndOpenedByCard(String rideId) {
//        CustomerChargeReturnResponse chargeReturnResponse = TestUtil.getCustomerChargeReturnResponse();
//        Ride ride = TestUtil.getOpenedByCardRideWithRideId(rideId);
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(rideId);
//        doReturn(chargeReturnResponse)
//                .when(paymentService)
//                .returnCharge(ride.getChargeId());
//    }
//
//    @When("closeRide method is called with Ride ID {string}")
//    public void closeRideMethodIsCalledWithRideId(String rideId) {
//        try {
//            actualCloseRideResponse = rideService.closeRide(rideId);
//        } catch (RuntimeException ex) {
//            exception = ex;
//        }
//    }
//
//    @Then("CloseRideResponse should contains Card Payment Method and CustomerChargeReturnResponse should not be null for Ride ID {string}")
//    public void closeRideResponseShouldContainsCardPaymentMethodAndCustomerChargeReturnResponseShouldNotBeNull
//                                                                                                    (String rideId) {
//        assertNull(exception);
//        assertEquals(actualCloseRideResponse.getRidePaymentMethod(), TestUtil.PAYMENT_CARD);
//        assertNotNull(actualCloseRideResponse.getCustomerChargeReturnResponse());
//    }
//
//    @Given("Ride with ID {string} exists and opened by cash")
//    public void rideWithIdExistsAndOpenedByCash(String rideId) {
//        Ride ride = TestUtil.getOpenedByCashRideWithRideId(rideId);
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(rideId);
//    }
//
//    @Then("CloseRideResponse should contains Cash Payment Method for Ride ID {string}")
//    public void closeRideResponseShouldContainsCashPaymentMethod(String rideId) {
//        assertNull(exception);
//        assertEquals(actualCloseRideResponse.getRidePaymentMethod(), TestUtil.PAYMENT_CASH);
//    }
//
//    @Given("Ride with ID {string} exists and not opened")
//    public void rideWithIdExistsAndNotOpened(String rideId) {
//        Ride ride = TestUtil.getFinishedRideWithRideId(rideId);
//
//        doReturn(Optional.of(ride))
//                .when(rideRepository)
//                .findById(rideId);
//    }
//
//    @Then("RideIsNotOpenedException should be thrown for Ride ID {string}")
//    public void rideIsNotOpenedExceptionThrownForId(String id) {
//        RideIsNotOpenedException expected = new RideIsNotOpenedException();
//
//        assertEquals(exception.getMessage(), expected.getMessage());
//    }
//
//    @Given("ChangeRideRequest with Action {string} and existing Ride ID {string}")
//    public void changeRideRequestWithActionAndExistingRideId(String rideAction, String rideId) {
//        actualRide = TestUtil.getOpenedByCashRideWithRideId(rideId);
//
//        doReturn(Optional.of(actualRide))
//                .when(rideRepository)
//                .findById(rideId);
//    }
//
//    @When("changeRideStatus is called with Action {string} and Ride ID {string}")
//    public void changeRideStatusIsCalledWithActionAndRideId(String rideAction, String rideId) {
//        try {
//            rideService.changeRideStatus(TestUtil.getChangeRideStatusRequestWithParameters(rideId, rideAction));
//        } catch (RuntimeException ex) {
//            exception = ex;
//        }
//    }
//
//    @Then("Ride with ID {string} should change status to ACCEPTED")
//    public void rideWithIdShouldChangeStatusToAccepted(String rideId) {
//        assertNull(exception);
//        assertEquals(actualRide.getStatus(), TestUtil.ACCEPTED_STATUS);
//    }
//
//    @Given("ChangeRideRequest with invalid Action {string} and existing Ride ID {string}")
//    public void changeRideRequestWithInvalidActionAndExistingRideId(String rideAction, String rideId) {
//        actualRide = TestUtil.getOpenedByCashRideWithRideId(rideId);
//
//        doReturn(Optional.of(actualRide))
//                .when(rideRepository)
//                .findById(rideId);
//    }
//
//    @Then("RideIsNotStartedException should be thrown for Ride ID {string}")
//    public void rideIsNotStartedExceptionThrownForId(String id) {
//        RideIsNotStartedException expected = new RideIsNotStartedException();
//
//        assertEquals(exception.getMessage(), expected.getMessage());
//    }
}
