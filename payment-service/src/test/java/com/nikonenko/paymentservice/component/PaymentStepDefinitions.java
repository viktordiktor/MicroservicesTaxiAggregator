package com.nikonenko.paymentservice.component;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.repositories.CustomerUserRepository;
import com.nikonenko.paymentservice.services.impl.PaymentGeneralServiceImpl;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.nikonenko.paymentservice.utils.TestUtil;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @InjectMocks
    private PaymentGeneralServiceImpl paymentGeneralService;
    private TokenResponse actualTokenResponse;
    private ChargeResponse actualChargeResponse;
    private CouponResponse actualCouponResponse;
    private RuntimeException exception;

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
        assertEquals(actualCouponResponse.getMonthDuration(), monthDuration);
        assertEquals(actualCouponResponse.getPercent(), new BigDecimal(percent));
    }
}
