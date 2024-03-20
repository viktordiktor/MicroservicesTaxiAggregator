package com.nikonenko.paymentservice.component;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.exceptions.CustomerAlreadyExistsException;
import com.nikonenko.paymentservice.exceptions.CustomerNotFoundException;
import com.nikonenko.paymentservice.exceptions.InsufficientFundsException;
import com.nikonenko.paymentservice.models.CustomerUser;
import com.nikonenko.paymentservice.repositories.CustomerUserRepository;
import com.nikonenko.paymentservice.services.impl.PaymentCustomerServiceImpl;
import com.nikonenko.paymentservice.services.impl.PaymentGeneralServiceImpl;
import com.nikonenko.paymentservice.utils.PaymentCoefficientUtil;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.nikonenko.paymentservice.utils.TestUtil;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import com.stripe.param.CouponCreateParams;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

@CucumberContextConfiguration
public class PaymentStepDefinitions {
    @Mock
    private CustomerUserRepository customerUserRepository;
    @Mock
    private StripeUtil stripeUtil;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PaymentCoefficientUtil paymentCoefficientUtil;
    @InjectMocks
    private PaymentGeneralServiceImpl paymentGeneralService;
    @InjectMocks
    private PaymentCustomerServiceImpl paymentCustomerService;
    private TokenResponse actualTokenResponse;
    private ChargeResponse actualChargeResponse;
    private CouponResponse actualCouponResponse;
    private CustomerCreationResponse actualCustomerCreationResponse;
    private CustomerChargeResponse actualCustomerChargeResponse;
    private CustomerCalculateRideResponse actualCustomerCalculateRideResponse;
    private RuntimeException exception;

    @Given("Customer not exists with Username {string} and phone {string} and Passenger ID {string} and Amount {string}")
    public void customerNotExistsWithUsernameAndPhoneAndPassengerIdAndAmount(String username, String phone,
                                                                             String passengerId, String amount) {
        CustomerCreationRequest request = TestUtil
                .getCustomerCreationRequestWithParameters(username, phone, UUID.fromString(passengerId), amount);
        Customer customer = TestUtil.getCustomerWithParameters(username, phone, amount);

        doReturn(false)
                .when(customerUserRepository)
                .existsByPassengerId(UUID.fromString(passengerId));
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerCreation(request);
    }

    @When("createCustomer method is called with Username {string} and phone {string} and Passenger ID {string} and Amount {string}")
    public void createCustomerMethodIsCalledWithUsernameAndPhoneAndPassengerIdAndAmount
            (String username, String phone, String passengerId, String amount) {
        try {
            actualCustomerCreationResponse = paymentCustomerService.createCustomer(TestUtil
                    .getCustomerCreationRequestWithParameters(username, phone, UUID.fromString(passengerId), amount));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CustomerCreationResponse should contains Username {string} and Phone {string}")
    public void customerCreationResponseShouldContainsUsernameAndPhone(String username, String phone) {
        assertNull(exception);
        assertEquals(actualCustomerCreationResponse.getUsername(), username);
        assertEquals(actualCustomerCreationResponse.getPhone(), phone);
    }

    @Given("Customer exists with Passenger ID {string}")
    public void customerExistsWithPassengerId(String passengerId) {
        doReturn(true)
                .when(customerUserRepository)
                .existsByPassengerId(UUID.fromString(passengerId));
    }

    @Then("CustomerAlreadyExistsException should be thrown for Customer with Passenger ID {string}")
    public void CustomerAlreadyExistsExceptionShouldBeThrown(String passengerId) {
        CustomerAlreadyExistsException expected = new CustomerAlreadyExistsException();

        assertEquals(exception.getMessage(), expected.getMessage());
    }

    @Given("Customer enough funds charge with Amount {string} and Passenger ID {string} and Currency {string}")
    public void customerEnoughFundsChargeWithAmountAndPassengerIdAndCurrency(String amount,
                                                                             String passengerId, String currency) {
        CustomerChargeRequest request = TestUtil
                .getCustomerChargeRequestWithParameters(amount, UUID.fromString(passengerId), currency);
        CustomerUser customerUser = TestUtil.getCustomerUserWithPassengerId(UUID.fromString(passengerId));
        Customer customer = TestUtil.getCustomer();
        PaymentIntent customerCharge = TestUtil.getPaymentIntentWithParameters(amount, currency);

        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByPassengerId(request.getPassengerId());
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerRetrieving(customerUser.getCustomerId());
        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentConfirming(request, customerUser.getCustomerId());
    }

    @When("createCustomerCharge method is called with Amount {string} and Passenger ID {string} and Currency {string}")
    public void createCustomerChargeMethodIsCalledWithAmountAndPassengerIdAndCurrency
                                                                (String amount, String passengerId, String currency) {
        try {
            actualCustomerChargeResponse = paymentCustomerService.createCustomerCharge(TestUtil
                    .getCustomerChargeRequestWithParameters(amount, UUID.fromString(passengerId), currency));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CustomerChargeResponse should contains Amount {string} and Passenger ID {string} and Currency {string}")
    public void customerChargeResponseShouldContainsAmountAndPassengerIdAndCurrency
                                                                (String amount, String passengerId, String currency) {
        assertNull(exception);
        assertEquals(actualCustomerChargeResponse.getAmount(), new BigDecimal(amount));
        assertEquals(actualCustomerChargeResponse.getPassengerId(), UUID.fromString(passengerId));
        assertEquals(actualCustomerChargeResponse.getCurrency(), currency);
    }

    @And("CustomerChargeResponse should be successful for Customer With Passenger ID {string}")
    public void customerChargeResponseShouldBeSuccessful(String passengerId) {
        assertTrue(actualCustomerChargeResponse.isSuccess());
    }

    @Given("Customer not enough funds charge with Amount {string} and Passenger ID {string} and Currency {string}")
    public void customerNotEnoughFundsChargeWithAmountAndPassengerIdAndCurrency(String amount,
                                                                                String passengerId, String currency) {
        CustomerUser customerUser = TestUtil.getCustomerUserWithPassengerId(UUID.fromString(passengerId));
        Customer customer = TestUtil.getCustomerWithZeroBalance();

        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByPassengerId(UUID.fromString(passengerId));
        doReturn(customer)
                .when(stripeUtil)
                .stripeCustomerRetrieving(customerUser.getCustomerId());
    }

    @Then("InsufficientFundsException should be Thrown for Customer With Passenger ID {string}")
    public void insufficientFundsExceptionShouldBeThrownForCustomerWithPassengerID(String passengerId) {
        InsufficientFundsException expected = new InsufficientFundsException();

        assertEquals(expected.getMessage(), exception.getMessage());
    }

    @Given("Customer not exists with Passenger ID {string}")
    public void customerNotExistsWithPassengerId(String passengerId) {
        doReturn(Optional.empty())
                .when(customerUserRepository)
                .findByPassengerId(UUID.fromString(passengerId));
    }

    @Then("CustomerNotFoundException should be thrown")
    public void customerNotFoundExceptionShouldBeThrownForPassengerWithId() {
        CustomerNotFoundException expected = new CustomerNotFoundException();

        assertEquals(expected.getMessage(), exception.getMessage());
    }

    @Given("Charge ID {string} and Customer exists")
    public void chargeIdAndCustomerExists(String chargeId) {
        PaymentIntent customerCharge = TestUtil.getPaymentIntentWithId(chargeId);
        CustomerUser customerUser = TestUtil.getCustomerUser();

        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentRetrieving(chargeId);
        doReturn(Optional.of(customerUser))
                .when(customerUserRepository)
                .findByCustomerId(customerCharge.getCustomer());
    }

    @When("getCustomerCharge method is called with Charge ID {string}")
    public void getCustomerChargeMethodIsCalledWithChargeId(String chargeId) {
        try {
            actualCustomerChargeResponse = paymentCustomerService.getCustomerCharge(chargeId);
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CustomerChargeResponse should contains charge with Charge ID {string}")
    public void customerChargeResponseShouldContainsChargeWithChargeId(String chargeId) {
        assertNull(exception);
        assertEquals(actualCustomerChargeResponse.getId(), chargeId);
    }

    @Given("Charge ID {string} and Customer not exists")
    public void chargeIdAndCustomerNotExists(String chargeId) {
        PaymentIntent customerCharge = TestUtil.getPaymentIntentWithId(chargeId);

        doReturn(customerCharge)
                .when(stripeUtil)
                .stripeIntentRetrieving(chargeId);
        doReturn(Optional.empty())
                .when(customerUserRepository)
                .findByCustomerId(customerCharge.getCustomer());
    }

    @Given("Ride Length {double} and Ride DateTime {string} and Coupon {string}")
    public void rideLengthAndRideDateTimeAndCoupon(Double rideLength, String rideDateTime, String couponId) {
        Coupon coupon = TestUtil.getCouponWithId(couponId);

        doCallRealMethod().when(paymentCoefficientUtil).getTimeCoefficient(LocalDateTime.parse(rideDateTime));
        doCallRealMethod().when(paymentCoefficientUtil).getDayCoefficient(LocalDateTime.parse(rideDateTime));
        doReturn(coupon)
                .when(stripeUtil)
                .retrieveCoupon(couponId, LocalDateTime.parse(rideDateTime));
    }

    @When("calculateRidePriceMethod is called with Ride Length {double} and Ride DateTime {string} and Coupon {string}")
    public void calculateRidePriceMethodIsCalledWithRideLengthAndRideDateTimeAndCoupon
                                                            (Double rideLength, String rideDateTime, String couponId) {
        try {
            actualCustomerCalculateRideResponse = paymentCustomerService
                    .calculateRidePrice(rideLength, LocalDateTime.parse(rideDateTime), couponId.equals("null")
                            ? null
                            : couponId);
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("CustomerCalculateRideResponse should contains Ride Length {double} and Ride DateTime {string}")
    public void customerCalculateRideResponseShouldContainsRideLengthAndRideDateTime(Double rideLength,
                                                                                      String rideDateTime) {
        assertNull(exception);
        assertEquals(actualCustomerCalculateRideResponse.getRideLength(), rideLength);
        assertEquals(actualCustomerCalculateRideResponse.getRideDateTime(), LocalDateTime.parse(rideDateTime));
    }

    @And("CustomerCalculateRideResponse should contains Coupon {string}")
    public void customerCalculateRideResponseShouldContainsCoupon(String coupon) {
        assertEquals(actualCustomerCalculateRideResponse.getCoupon(), coupon);
    }

    @And("CustomerCalculateRideResponse should contains Not Null Ride Price")
    public void customerCalculateRideResponseShouldContainsNotNullRidePrice() {
        assertNotNull(actualCustomerCalculateRideResponse.getPrice());
    }

    @Given("Ride Length {double} and Ride DateTime {string}")
    public void rideLengthAndRideDateTime(Double rideLength, String rideDateTime) {
        doCallRealMethod().when(paymentCoefficientUtil).getTimeCoefficient(LocalDateTime.parse(rideDateTime));
        doCallRealMethod().when(paymentCoefficientUtil).getDayCoefficient(LocalDateTime.parse(rideDateTime));
    }

    @Given("CardRequest of Card Number {string} and Exp Month {int} and Exp Year {int} and CVC {int}")
    public void CardRequestOfCardNumberAndExpMonthAndExpYearAndCvc(String cardNumber,
                                                                   Integer expMonth, Integer expYear, Integer cvc) {
        Map<String, Object> cardParams = TestUtil.getCardParamsWithParameters(cardNumber, expMonth, expYear, cvc);
        Token token = TestUtil.getToken();
        CardRequest request = TestUtil.getCardRequestWithParameters(cardNumber, expMonth, expYear, cvc);

        doReturn(cardParams)
                .when(stripeUtil)
                .createCardParams(request);
        doReturn(token)
                .when(stripeUtil)
                .stripeCreateToken(cardParams);
    }

    @When("generateTokenByCard is called with Card Request of Card Number {string} and Exp Month {int} and Exp Year {int} and CVC {int}")
    public void generateTokenByCardIsCalledWithCardRequestOfCardNumberAndExpMonthAndExpYearAndCvc
            (String cardNumber, Integer expMonth, Integer expYear, Integer cvc) {
        try {
            actualTokenResponse = paymentGeneralService.generateTokenByCard(TestUtil
                    .getCardRequestWithParameters(cardNumber, expMonth, expYear, cvc));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("TokenResponse should contains Token and Card Number {string}")
    public void tokenResponseShouldContainsTokenResponseWithTokenAndCardNumber(String cardNumber) {
        TokenResponse expected = TestUtil.getTokenResponseWithCardNumber(cardNumber);

        assertNull(exception);
        assertEquals(expected, actualTokenResponse);
    }

    @Given("ChargeRequest of Token {string} and currency {string} and amount {string}")
    public void chargeRequestOfTokenAndCurrencyAndAmount(String token, String currency, String amount) {
        ChargeRequest request = TestUtil.getChargeRequestWithParameters(token, currency, amount);
        ChargeResponse initialResponse = TestUtil.getInitialChargeResponseWithAmount(amount);
        Map<String, Object> chargeParams = TestUtil.getChargeParamsWithParams(token, currency, amount);
        Charge charge = TestUtil.getChargeWithAmount(amount);

        doReturn(initialResponse)
                .when(modelMapper)
                .map(request, ChargeResponse.class);
        doReturn(chargeParams)
                .when(stripeUtil)
                .createChargeParams(request);
        doReturn(charge)
                .when(stripeUtil)
                .stripeChargeCreation(chargeParams);
        doCallRealMethod().when(stripeUtil).setChargeResponse(charge, initialResponse);
    }

    @When("charge method is called with Token {string} and currency {string} and amount {string}")
    public void chargeMethodIsCalledWithTokenAndCurrencyAndAmount(String token, String currency, String amount) {
        try {
            actualChargeResponse = paymentGeneralService.charge(TestUtil
                    .getChargeRequestWithParameters(token, currency, amount));
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("ChargeResponse should contains amount {string} for token {string}")
    public void chargeResponseShouldContainsAmountForToken(String amount, String token) {
        assertNull(exception);
        assertEquals(actualChargeResponse.getAmount(), new BigDecimal(amount));
    }

    @And("ChargeResponse should be successful for token {string}")
    public void chargeResponseShouldBeSuccessfulForToken(String token) {
        assertTrue(actualChargeResponse.getSuccess());
    }

    @Given("Charge valid ID {string}")
    public void chargeValidId(String chargeId) {
        Charge charge = TestUtil.getChargeWithChargeId(chargeId);

        doReturn(charge)
                .when(stripeUtil)
                .stripeReceivingCharge(charge.getId());
    }

    @When("getChargeById method is called with Charge ID {string}")
    public void getChargeByIdMethodIsCalledWithChargeId(String chargeId) {
        try {
            actualChargeResponse = paymentGeneralService.getChargeById(chargeId);
        } catch (RuntimeException ex) {
            exception = ex;
        }
    }

    @Then("ChargeResponse should contains Charge ID {string}")
    public void chargeResponseShouldContainsChargeId(String chargeId) {
        assertNull(exception);
        assertEquals(actualChargeResponse.getChargeId(), chargeId);
    }

    @Given("CouponRequest of Month Duration {long} and Percent {string}")
    public void couponRequestOfMonthDurationAndPercent(Long monthDuration, String percent) {
        Coupon coupon = TestUtil.getCouponWithParameters(monthDuration, percent);

        doReturn(coupon)
                .when(stripeUtil)
                .stripeCouponCreation(any(CouponCreateParams.class));
    }

    @When("createCoupon method is called with Month Duration {long} and Percent {string}")
    public void createCouponMethodIsCalledWithMonthDurationAndPercent(Long monthDuration, String percent) {
        actualCouponResponse = paymentGeneralService.createCoupon(TestUtil
                .getCouponRequestWithParameters(monthDuration, percent));
    }

    @Then("CouponResponse should contains Month Duration {long} and Percent {string}")
    public void couponResponseShouldContainsMonthDurationAndPercent(Long monthDuration, String percent) {
        assertNull(exception);
        assertEquals(actualCouponResponse.getMonthDuration(), monthDuration);
        assertEquals(actualCouponResponse.getPercent(), new BigDecimal(percent));
    }
}
