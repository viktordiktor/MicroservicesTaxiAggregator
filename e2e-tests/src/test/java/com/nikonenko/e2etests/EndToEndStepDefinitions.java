package com.nikonenko.e2etests;

import com.nikonenko.e2etests.clients.DriverServiceClient;
import com.nikonenko.e2etests.clients.PassengerServiceClient;
import com.nikonenko.e2etests.clients.PaymentServiceClient;
import com.nikonenko.e2etests.clients.RideServiceClient;
import com.nikonenko.e2etests.dto.CalculateDistanceRequest;
import com.nikonenko.e2etests.dto.CalculateDistanceResponse;
import com.nikonenko.e2etests.dto.CloseRideResponse;
import com.nikonenko.e2etests.dto.CreateRideRequest;
import com.nikonenko.e2etests.dto.CustomerCalculateRideRequest;
import com.nikonenko.e2etests.dto.CustomerCalculateRideResponse;
import com.nikonenko.e2etests.dto.CustomerChargeRequest;
import com.nikonenko.e2etests.dto.CustomerChargeResponse;
import com.nikonenko.e2etests.dto.CustomerCreationRequest;
import com.nikonenko.e2etests.dto.CustomerExistsResponse;
import com.nikonenko.e2etests.dto.PageResponse;
import com.nikonenko.e2etests.dto.RideResponse;
import com.nikonenko.e2etests.kafka.producer.CustomerCreationRequestProducer;
import com.nikonenko.e2etests.kafka.producer.DriverReviewRequestProducer;
import com.nikonenko.e2etests.kafka.producer.PassengerReviewRequestProducer;
import com.nikonenko.e2etests.kafka.producer.RideStatusRequestProducer;
import com.nikonenko.e2etests.models.RidePaymentMethod;
import com.nikonenko.e2etests.models.RideStatus;
import com.nikonenko.e2etests.utils.TestUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.jdbc.Sql;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@Slf4j
public class EndToEndStepDefinitions {
    private final RideServiceClient rideServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final CustomerCreationRequestProducer customerCreationRequestProducer;
    private final RideStatusRequestProducer rideStatusRequestProducer;
    private final DriverReviewRequestProducer driverReviewRequestProducer;
    private final PassengerReviewRequestProducer passengerReviewRequestProducer;

    private Long passengerId;
    private Long driverId;
    private RuntimeException exception;
    private PageResponse<RideResponse> actualPageRideResponse;
    private CalculateDistanceRequest calculateDistanceRequest;
    private CalculateDistanceResponse calculateDistanceResponse;
    private CustomerCalculateRideRequest calculateRideRequest;
    private CustomerCalculateRideResponse calculateRideResponse;
    private CustomerExistsResponse customerExistsResponse;
    private CustomerCreationRequest customerCreationRequest;
    private CustomerChargeRequest customerChargeRequest;
    private CustomerChargeResponse customerChargeResponse;
    private CreateRideRequest createRideRequest;
    private RideResponse rideResponse;
    private CloseRideResponse closeRideResponse;

    @Given("Passenger with ID {long} exists")
    public void passengerWithIdExists(Long passengerId) {
        this.passengerId = passengerId;
    }

    @When("getRidesByPassengerId is called by Ride Service Client")
    public void getRidesByPassengerIdIsCalledByRideServiceClient() {
        actualPageRideResponse = rideServiceClient.getRidesByPassengerId(passengerId);
    }

    @Then("PageRideResponse should not be null")
    public void pageRideResponseShouldNotBeNullForPassengerWithId() {
        assertNotNull(actualPageRideResponse);
    }

    @And("PageRideResponse should contains at least one RideResponse")
    public void pageRideResponseShouldContainsRideResponse() {
        assertTrue(actualPageRideResponse.getTotalElements() > 0);
    }

    @And("All Ride Responses should contains Passenger ID")
    public void allRideResponsesShouldContainsPassengerId() {
        for (RideResponse rideResponse : actualPageRideResponse.getObjectList()) {
            assertEquals(rideResponse.getPassengerId(), passengerId);
        }
    }

    @Given("Driver with ID {long} exists")
    public void driverWithIdExists(Long driverId) {
        this.driverId = driverId;
    }

    @When("getRidesByDriverId is called by Ride Service Client")
    public void getRidesByDriverIdIsCalledByRideServiceClient() {
        actualPageRideResponse = rideServiceClient.getRidesByDriverId(driverId);
    }

    @And("All Ride Responses should contains Driver ID")
    public void allRideResponsesShouldContainsDriverId() {
        for (RideResponse rideResponse : actualPageRideResponse.getObjectList()) {
            assertEquals(rideResponse.getDriverId(), driverId);
        }
    }

    @Given("StartGeo {string} and EndGeo {string}")
    public void startGeoAndEndGeo(String startGeo, String endGeo) {
        calculateDistanceRequest = TestUtil.createCalculateDistanceRequest(startGeo, endGeo);
    }

    @When("calculateDistance method is called with CalculateDistanceRequest")
    public void calculateDistanceMethodIsCalledWithCalculateDistanceRequest() {
        calculateDistanceResponse = rideServiceClient.calculateDistance(calculateDistanceRequest);
    }

    @Then("CalculateDistanceResponse should contains Not Null Distance")
    public void calculateDistanceResponseShouldContainsNotNullDistance() {
        assertNotNull(calculateDistanceResponse.getDistance());
    }

    @And("CalculateDistanceResponse should not contains Error Message")
    public void calculateDistanceResponseShouldNotContainsErrorMessage() {
        assertNull(calculateDistanceResponse.getErrorMessage());
    }

    @Given("Ride Length {double} and Ride DateTime {string} and Valid Coupon {string}")
    public void rideLengthAndRideDateTimeAndCoupon(Double rideLength, String rideDateTime, String coupon) {
        calculateRideRequest = TestUtil.getCalculateRideRequest(rideLength, rideDateTime, coupon);
    }

    @When("calculateRidePrice method is called with CalculateRideRequest")
    public void calculateRidePriceMethodIsCalledWithCalculateRideRequest() {
        calculateRideResponse = paymentServiceClient.calculateRidePrice(calculateRideRequest);
    }

    @Then("CustomerCalculateRideResponse should contains Not Null Price")
    public void customerCalculateRideResponseShouldContainsNotNullPrice() {
        assertNotNull(calculateRideResponse.getPrice());
    }

    @And("CustomerCalculateRideResponse should not contains Error Message")
    public void customerCalculateRideResponseShouldNotContainsErrorMessage() {
        assertNull(calculateRideResponse.getErrorMessage());
    }


    @When("isCustomerExists method is called by Payment Service Client")
    public void isCustomerExistsMethodIsCalledWithPassengerID() {
        customerExistsResponse = paymentServiceClient.isCustomerExists(passengerId);
    }

    @Then("CustomerExistsResponse should be False")
    public void customerExistsResponseShouldBeFalse() {
        assertFalse(customerExistsResponse.isExists());
    }

    @Given("Username {string} and phone {string} and Existing Passenger ID {long} and Amount {string}")
    @Sql("classpath:db/delete-exist-customer-users.sql")
    public void usernameAndPhoneAndExistingPassengerIdAndAmount(String username, String phone,
                                                                Long passengerId, String amount) {
        this.passengerId = passengerId;
        customerCreationRequest = TestUtil.createCustomerCreationRequest(username, phone, passengerId, amount);
    }

    @When("sendCustomerCreationRequest method is called with CustomerCreationRequest")
    public void sendCustomerCreationRequestMethodIsCalledWithCustomerCreationRequest() {
        customerCreationRequestProducer.sendCustomerCreationRequest(customerCreationRequest);
    }

    @Then("CustomerExistsResponse should be True after 10 seconds")
    public void customerExistsResponseShouldBeTrueAfterTenSeconds() {
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> assertTrue(paymentServiceClient.isCustomerExists(passengerId).isExists()));
    }

    @Then("CustomerExistsResponse should be True")
    public void customerExistsResponseShouldBeTrue() {
        assertTrue(customerExistsResponse.isExists());
    }

    @Given("Valid CustomerChargeRequest of Currency {string}")
    public void validCustomerChargeRequest(String currency) {
        customerChargeRequest = TestUtil
                .getCustomerChargeRequest(passengerId, currency, calculateRideResponse.getPrice());
    }

    @When("customerCharge method is called with customerChargeRequest")
    public void customerChargeMethodIsCalledWithCustomerChargeRequest() {
        try {
            customerChargeResponse = paymentServiceClient.customerCharge(customerChargeRequest);
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("Customer Charge should have Not Null ID")
    public void customerChargeShouldHaveNotNullId() {
        assertNotNull(customerChargeResponse.getId());
    }

    @And("Customer Charge should have True success")
    public void customerChargeShouldHaveTrueSuccess() {
        assertTrue(customerChargeResponse.isSuccess());
    }

    @And("Customer Charge should Not have Error Message")
    public void customerChargeShouldNotHaveErrorMessage() {
        assertNull(exception);
        assertNull(customerChargeResponse.getErrorMessage());
    }

    @Given("Valid CreateRideRequest by Card of Start Address {string} and End Address {string}")
    public void validCreateRideRequestByCard(String startAddress, String endAddress) {
        createRideRequest = TestUtil.getCreateRideRequestByCard(customerChargeResponse.getId(),
                calculateRideResponse.getRideLength(), startAddress, endAddress, passengerId);
    }

    @When("createRideRequest method is called with createRideRequest")
    public void createRideRequestMethodIsCalledWithCreateRideRequest() {
        rideResponse = rideServiceClient.createRideRequest(createRideRequest);
    }

    @Then("Ride Response should contains Not Null ID")
    public void rideResponseShouldContainsNotNullId() {
        assertNotNull(rideResponse.getId());
    }

    @And("Ride Response should Not have Error Message")
    public void rideResponseShouldNotHaveErrorMessage() {
        assertNull(rideResponse.getErrorMessage());
    }

    @And("Ride Response should have By Card Payment method")
    public void rideResponseShouldHaveByCardPaymentMethod() {
        assertEquals(rideResponse.getPaymentMethod(), RidePaymentMethod.BY_CARD);
    }

    @When("closeRide method is called with Ride ID")
    public void closeRideMethodIsCalledWithRideId() {
        closeRideResponse = rideServiceClient.closeRide(rideResponse.getId());
    }

    @Then("CloseRideResponse should Card Payment Method")
    public void closeRideResponseShouldCardPaymentMethod() {
        assertEquals(closeRideResponse.getRidePaymentMethod(), RidePaymentMethod.BY_CARD);
    }

    @And("CloseRideResponse should contains valid CustomerChargeReturnResponse")
    public void closeRideResponseShouldContainsValidCustomerChargeReturnResponse() {
        assertEquals(closeRideResponse.getCustomerChargeReturnResponse().getPaymentId(), rideResponse.getChargeId());
    }

    @Given("Valid CreateRideRequest by Cash of Start Address {string} and End Address {string} and Passenger with ID {long}")
    public void validCreateRideRequestByCash(String startAddress, String endAddress, Long passengerId) {
        createRideRequest = TestUtil.getCreateRideRequestByCash(calculateRideResponse.getRideLength(), startAddress,
                endAddress, passengerId);
    }

    @And("Ride Response should have By Cash Payment method")
    public void rideResponseShouldHaveByCashPaymentMethod() {
        assertEquals(rideResponse.getPaymentMethod(), RidePaymentMethod.BY_CASH);
    }

    @Then("CloseRideResponse should Cash Payment Method")
    public void closeRideResponseShouldCashPaymentMethod() {
        assertEquals(closeRideResponse.getRidePaymentMethod(), RidePaymentMethod.BY_CASH);
    }

    @And("CloseRideResponse should not contains CustomerChargeReturnResponse")
    public void closeRideResponseShouldNptContainsCustomerChargeReturnResponse() {
        assertNull(closeRideResponse.getCustomerChargeReturnResponse());
    }

    @When("sendChangeRideStatusRequest method is called with Ride Action {string}")
    public void sendChangeRideStatusRequestMethodIsCalledWithRideAction(String action) {
        rideStatusRequestProducer
                .sendChangeRideStatusRequest(TestUtil.getChangeRideStatusRequest(rideResponse.getId(), action, driverId));
    }

    @Then("Ride should change status to {string}")
    public void rideShouldChangeStatusTo(String status) {
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> assertEquals(RideStatus.valueOf(status),
                        rideServiceClient.getRide(rideResponse.getId()).getStatus()));
    }

    @When("sendRatingDriverRequest method is called with Rating {int} and Comment {string}")
    public void sendRatingDriverRequestMethodIsCalledWithRatingAndComment(Integer rating, String comment) {
        passengerReviewRequestProducer
                .sendRatingDriverRequest(TestUtil.getReviewRequest(rideResponse.getId(), rating, comment));
    }

    @Then("Driver should contains new Review")
    public void driverShouldContainsNewReview() {
        int reviewsSize = driverServiceClient.getDriver(driverId).getRatingSet().size();
        log.info("Review Size: {}", reviewsSize);
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> assertEquals(reviewsSize + 1,
                        driverServiceClient.getDriver(driverId).getRatingSet().size()));
    }


    @When("sendRatingPassengerRequest method is called with Rating {int} and Comment {string}")
    public void sendRatingPassengerRequestMethodIsCalledWithRatingAndComment(Integer rating, String comment) {
        driverReviewRequestProducer
                .sendRatingPassengerRequest(TestUtil.getReviewRequest(rideResponse.getId(), rating, comment));
    }

    @Then("Passenger should contains new Review")
    public void passengerShouldContainsNewReview() {
        int reviewsSize = passengerServiceClient.getPassenger(passengerId).getRatingSet().size();
        log.info("Review Size: {}", reviewsSize);
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> assertEquals(reviewsSize + 1,
                        passengerServiceClient.getPassenger(passengerId).getRatingSet().size()));
    }

    @Then("FeignClientException should be Thrown with message {string}")
    public void feignClientExceptionShouldBeThrownWithMessage(String exceptionMessage) {
        assertEquals(exceptionMessage, exception.getMessage());
    }
}
