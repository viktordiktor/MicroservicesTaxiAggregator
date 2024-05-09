package com.nikonenko.rideservice.component;

import com.nikonenko.rideservice.dto.CloseRideResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.RideResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.exceptions.ChargeIsNotSuccessException;
import com.nikonenko.rideservice.exceptions.RideIsNotOpenedException;
import com.nikonenko.rideservice.exceptions.RideIsNotStartedException;
import com.nikonenko.rideservice.exceptions.RideNotFoundException;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.repositories.RideRepository;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.services.impl.RideServiceImpl;
import com.nikonenko.rideservice.utils.TestUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

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

    private Ride expectedRide;
    private RideResponse expectedResponse;
    private Mono<RideResponse> rideResponse;
    private Mono<CloseRideResponse> closeRideResponse;

    @Given("Ride with ID {string} exists")
    public void rideWithIdExists(String id) {
        expectedRide = TestUtil.getFinishedRide();
        expectedResponse = TestUtil.getRideResponse();

        doReturn(Mono.justOrEmpty(expectedRide))
                .when(rideRepository)
                .findById(id);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(expectedRide, RideResponse.class);
    }

    @When("getRideById method is called with ID {string}")
    public void findByIdMethodIsCalledWithId(String id) {
        rideResponse = rideService.getRideById(id);
    }

    @Then("RideResponse should contains Ride with ID {string}")
    public void rideResponseShouldContainsRideWithId(String id) {
        StepVerifier.create(rideResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Given("Ride with ID {string} not exists")
    public void rideWithIdNotExists(String id) {
        doReturn(Mono.empty())
                .when(rideRepository)
                .findById(id);
    }

    @Then("RideNotFoundException should be thrown for Ride ID {string}")
    public void rideNotFoundExceptionThrownForId(String id) {
        StepVerifier.create(rideService.getRideById(id))
                .expectError(RideNotFoundException.class)
                .verify();
    }

    @Given("CreateRideRequest by card of Passenger ID {string} and Start Address {string} and End Address {string} and success Charge ID {string}")
    public void createRideRequestByCardOfPassengerIDAndStartAddressAndEndAddressAndSuccessChargeID
            (String passengerId, String startAddress, String endAddress, String chargeId) {
        expectedResponse = TestUtil
                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        CreateRideRequest request = TestUtil
                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        Ride notSavedRide = TestUtil
                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        Ride savedRide = TestUtil
                .getOpenedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        CustomerChargeResponse chargeResponse = TestUtil.getSuccessfulCustomerChargeResponse();

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(chargeResponse)
                .when(paymentService)
                .getChargeById(chargeId);
        doReturn(Mono.just(savedRide))
                .when(rideRepository)
                .save(notSavedRide);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(savedRide, RideResponse.class);
    }

    @When("createRide method is called with Passenger ID {string} and Start Address {string} and End Address {string} and Charge ID {string}")
    public void createRideMethodIsCalledWithPassengerIdAndStartAddressAndEndAddressAndChargeId
            (String passengerId, String startAddress, String endAddress, String chargeId) {
        rideResponse = rideService.createRide(TestUtil
                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId));
    }

    @Then("RideResponse should contains Passenger ID {string} and Start Address {string} and End Address {string} and Charge ID {string}")
    public void rideResponseShouldContainsPassengerIdAndStartAddressAndEndAddressAndChargeId
            (String passengerId, String startAddress, String endAddress, String chargeId) {
        StepVerifier.create(rideResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Given("CreateRideRequest by card of Passenger ID {string} and Start Address {string} and End Address {string} and unsuccessful Charge ID {string}")
    public void createRideRequestByCardOfPassengerIDAndStartAddressAndEndAddressAndUnsuccessfulChargeID
            (String passengerId, String startAddress, String endAddress, String chargeId) {
        CreateRideRequest request = TestUtil
                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        Ride notSavedRide = TestUtil
                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId);
        CustomerChargeResponse chargeResponse = TestUtil.getUnsuccessfulCustomerChargeResponse();

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(chargeResponse)
                .when(paymentService)
                .getChargeById(chargeId);
    }

    @Then("ChargeIsNotSuccessException should be thrown for Passenger ID {string} and Start Address {string} and End Address {string} and unsuccessful Charge ID {string}")
    public void chargeIsNotSuccessThrownForId(String passengerId, String startAddress, String endAddress, String chargeId) {
        StepVerifier.create(rideService.createRide(TestUtil
                        .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, chargeId)))
                .expectError(ChargeIsNotSuccessException.class)
                .verify();
    }

    @Given("CreateRideRequest by cash of Passenger ID {string} and Start Address {string} and End Address {string}")
    public void createRideRequestByCashOfPassengerIDAndStartAddressAndEndAddress(String passengerId, String startAddress,
                                                                                 String endAddress) {
        expectedResponse = TestUtil
                .getRideResponseWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
        CreateRideRequest request = TestUtil
                .getCreateRideRequestWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
        Ride notSavedRide = TestUtil
                .getNotSavedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);
        Ride savedRide = TestUtil
                .getOpenedRideWithParameters(UUID.fromString(passengerId), startAddress, endAddress, null);

        doReturn(notSavedRide)
                .when(modelMapper)
                .map(request, Ride.class);
        doReturn(Mono.just(savedRide))
                .when(rideRepository)
                .save(notSavedRide);
        doReturn(expectedResponse)
                .when(modelMapper)
                .map(savedRide, RideResponse.class);
    }

    @When("createRide method is called with Passenger ID {string} and Start Address {string} and End Address {string}")
    public void createRideMethodIsCalledWithPassengerIdAndStartAddressAndEndAddressAndChargeId
            (String passengerId, String startAddress, String endAddress) {
        rideResponse = rideService.createRide(TestUtil.getCreateRideRequestWithParameters(
                UUID.fromString(passengerId), startAddress, endAddress, null));
    }

    @Then("RideResponse should contains Passenger ID {string} and Start Address {string} and End Address {string}")
    public void rideResponseShouldContainsPassengerIdAndStartAddressAndEndAddress(String passengerId, String startAddress,
                                                                                  String endAddress) {
        StepVerifier.create(rideResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Given("Ride with ID {string} exists and opened by card")
    public void rideWithIdExistsAndOpenedByCard(String rideId) {
        CustomerChargeReturnResponse chargeReturnResponse = TestUtil.getCustomerChargeReturnResponse();
        expectedRide = TestUtil.getOpenedByCardRideWithRideId(rideId);

        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .findById(rideId);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(expectedRide);
        doReturn(chargeReturnResponse)
                .when(paymentService)
                .returnCharge(expectedRide.getChargeId());
    }

    @When("closeRide method is called with Ride ID {string}")
    public void closeRideMethodIsCalledWithRideId(String rideId) {
        closeRideResponse = rideService.closeRide(rideId);
    }

    @Then("CloseRideResponse should contains Card Payment Method and CustomerChargeReturnResponse should not be null for Ride ID {string}")
    public void closeRideResponseShouldContainsCardPaymentMethodAndCustomerChargeReturnResponseShouldNotBeNull
            (String rideId) {
        StepVerifier.create(closeRideResponse)
                .assertNext(response -> {
                    assertThat(response.getRidePaymentMethod()).isEqualTo(TestUtil.PAYMENT_CARD);
                    assertThat(response.getCustomerChargeReturnResponse()).isNotNull();
                })
                .verifyComplete();
    }

    @Given("Ride with ID {string} exists and opened by cash")
    public void rideWithIdExistsAndOpenedByCash(String rideId) {
        expectedRide = TestUtil.getOpenedByCashRideWithRideId(rideId);

        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .findById(rideId);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(expectedRide);
    }

    @Then("CloseRideResponse should contains Cash Payment Method for Ride ID {string}")
    public void closeRideResponseShouldContainsCashPaymentMethod(String rideId) {
        StepVerifier.create(closeRideResponse)
                .assertNext(response -> {
                    assertThat(response.getRidePaymentMethod()).isEqualTo(TestUtil.PAYMENT_CASH);
                    assertThat(response.getCustomerChargeReturnResponse()).isNull();
                })
                .verifyComplete();
    }

    @Given("Ride with ID {string} exists and not opened")
    public void rideWithIdExistsAndNotOpened(String rideId) {
        expectedRide = TestUtil.getFinishedRideWithRideId(rideId);

        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .findById(rideId);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(expectedRide);
    }

    @Then("RideIsNotOpenedException should be thrown for Ride ID {string}")
    public void rideIsNotOpenedExceptionThrownForId(String id) {
        StepVerifier.create(rideService.closeRide(id))
                .expectError(RideIsNotOpenedException.class)
                .verify();
    }

    @Given("ChangeRideRequest with Action {string} and existing Ride ID {string}")
    public void changeRideRequestWithActionAndExistingRideId(String rideAction, String rideId) {
        expectedRide = TestUtil.getOpenedByCashRideWithRideId(rideId);

        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .findById(rideId);
        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .save(expectedRide);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(expectedRide);
    }

    @When("changeRideStatus is called with Action {string} and existing Ride ID {string} and valid action")
    public void changeRideStatusIsCalledWithActionAndExistingRideIdAndValidAction(String rideAction, String rideId) {
        StepVerifier.create(rideService.changeRideStatus(TestUtil.getChangeRideStatusRequestWithParameters(rideId, rideAction)))
                .expectSubscription()
                .verifyComplete();
    }

    @When("changeRideStatus is called with Action {string} and existing Ride ID {string} and invalid action")
    public void changeRideStatusIsCalledWithActionAndExistingRideIdAndInvalidAction(String rideAction, String rideId) {
        StepVerifier.create(rideService.changeRideStatus(TestUtil.getChangeRideStatusRequestWithParameters(rideId, rideAction)))
                .expectSubscription()
                .verifyError();
    }

    @When("changeRideStatus is called with Action {string} and non-existing Ride ID {string}")
    public void changeRideStatusIsCalledWithActionAndNotExistingRideId(String rideAction, String rideId) {
        StepVerifier.create(rideService.changeRideStatus(TestUtil.getChangeRideStatusRequestWithParameters(rideId, rideAction)))
                .expectSubscription()
                .verifyError();
    }


    @Then("Ride with ID {string} should change status to ACCEPTED")
    public void rideWithIdShouldChangeStatusToAccepted(String rideId) {
        StepVerifier.create(rideRepository.findById(rideId))
                .expectSubscription()
                .assertNext(ride -> assertEquals(ride.getStatus(), TestUtil.ACCEPTED_STATUS))
                .verifyComplete();
    }

    @Given("ChangeRideRequest with invalid Action {string} and existing Ride ID {string}")
    public void changeRideRequestWithInvalidActionAndExistingRideId(String rideAction, String rideId) {
        expectedRide = TestUtil.getOpenedByCashRideWithRideId(rideId);

        doReturn(Mono.just(expectedRide))
                .when(rideRepository)
                .findById(rideId);
        doReturn(Mono.empty())
                .when(rideRepository)
                .delete(expectedRide);
    }

    @Then("RideIsNotStartedException should be thrown for Ride ID {string}")
    public void rideIsNotStartedExceptionThrownForId(String id) {
        StepVerifier.create(rideService.changeRideStatus(TestUtil
                        .getChangeRideStatusRequestWithParameters(id, TestUtil.RIDE_ACTION_FINISH.toString())))
                .expectSubscription()
                .expectError(RideIsNotStartedException.class)
                .verify();
    }
}
