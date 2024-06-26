package com.nikonenko.paymentservice.utils;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeReturnResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerExistsResponse;
import com.nikonenko.paymentservice.models.CustomerUser;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Token;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class TestUtil {
    public final String DEFAULT_TOKEN = "token1";
    public final String DEFAULT_CARD_NUMBER = "4242424242424242";
    public final int DEFAULT_EXP_MONTH = 10;
    public final int DEFAULT_EXP_YEAR = 25;
    public final int DEFAULT_CVC = 100;
    public final String DEFAULT_TOKEN_ID = "token1";
    public final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    public final String DEFAULT_CURRENCY = "USD";
    public final String DEFAULT_CHARGE_ID = "charge1";
    public final boolean TRUE_SUCCESS = true;
    public final String DEFAULT_MESSAGE = "succeeded";
    public final Long DEFAULT_MONTH_DURATION = 12L;
    public final BigDecimal DEFAULT_PERCENT = BigDecimal.valueOf(50L);
    public final String DEFAULT_COUPON_ID = "coupon1";
    public final String DEFAULT_CUSTOMER_ID = "customer1";
    public final UUID DEFAULT_PASSENGER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public final String DEFAULT_USERNAME = "username";
    public final String DEFAULT_PHONE = "+375111111111";
    public final BigDecimal DEFAULT_CUSTOMER_AMOUNT = BigDecimal.valueOf(100L);
    public final BigDecimal DEFAULT_CHARGE_AMOUNT = BigDecimal.valueOf(50L);
    public final BigDecimal INFLUENTIAL_CHARGE_AMOUNT = BigDecimal.valueOf(200L);
    public final Double DEFAULT_LENGTH = 10.0;
    public final LocalDateTime DEFAULT_DATETIME = LocalDateTime.of(2024, 2, 21, 12, 0);
    public final BigDecimal RIDE_PRICE_WITH_COUPON = new BigDecimal("2.50");
    public final BigDecimal RIDE_PRICE_WITHOUT_COUPON = new BigDecimal("5.00");
    public final String DEFAULT_REFUND_ID = "refund1";
    public final boolean CUSTOMER_EXISTS = true;
    public final boolean CUSTOMER_NOT_EXISTS = false;
    public final String DEFAULT_ALLOW_REDIRECTS = "never";
    public final String DEFAULT_PAYMENT_METHOD = "pm_card_visa";

    public CardRequest getCardRequest() {
        return CardRequest.builder()
                .cardNumber(DEFAULT_CARD_NUMBER)
                .expirationMonth(DEFAULT_EXP_MONTH)
                .expirationYear(DEFAULT_EXP_YEAR)
                .cvc(DEFAULT_CVC)
                .build();
    }

    public CardRequest getCardRequestWithParameters(String cardNumber,
                                                    Integer expMonth, Integer expYear, Integer cvc) {
        return CardRequest.builder()
                .cardNumber(cardNumber)
                .expirationMonth(expMonth)
                .expirationYear(expYear)
                .cvc(cvc)
                .build();
    }

    public TokenResponse getTokenResponse() {
        return TokenResponse.builder()
                .token(DEFAULT_TOKEN)
                .cardNumber(DEFAULT_CARD_NUMBER)
                .build();
    }

    public TokenResponse getTokenResponseWithCardNumber(String cardNumber) {
        TokenResponse tokenResponse = getTokenResponse();
        tokenResponse.setCardNumber(cardNumber);
        return tokenResponse;
    }

    public Map<String, Object> getCardParams() {
        return  Map.of(
                "number", DEFAULT_CARD_NUMBER,
                "exp_month", DEFAULT_EXP_MONTH,
                "exp_year" , DEFAULT_EXP_YEAR,
                "cvc", DEFAULT_CVC
        );
    }

    public Map<String, Object> getCardParamsWithParameters(String cardNumber,
                                                           Integer expMonth, Integer expYear, Integer cvc) {
        return  Map.of(
                "number", cardNumber,
                "exp_month", expMonth,
                "exp_year" , expYear,
                "cvc", cvc
        );
    }

    public Token getToken() {
        Token token = new Token();
        token.setId(DEFAULT_TOKEN_ID);
        return token;
    }

    public ChargeRequest getChargeRequest() {
        return ChargeRequest.builder()
                .amount(DEFAULT_AMOUNT)
                .currency(DEFAULT_CURRENCY)
                .stripeToken(DEFAULT_TOKEN_ID)
                .build();
    }

    public ChargeRequest getChargeRequestWithParameters(String token, String currency, String amount) {
        return ChargeRequest.builder()
                .amount(new BigDecimal(amount))
                .currency(currency)
                .stripeToken(token)
                .build();
    }

    public ChargeResponse getInitialChargeResponse() {
        return ChargeResponse.builder()
                .chargeId(DEFAULT_CHARGE_ID)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public ChargeResponse getInitialChargeResponseWithAmount(String amount) {
        ChargeResponse initialChargeResponse = getInitialChargeResponse();
        initialChargeResponse.setAmount(new BigDecimal(amount));
        return initialChargeResponse;
    }

    public ChargeResponse getChargeResponse() {
        return ChargeResponse.builder()
                .chargeId(DEFAULT_CHARGE_ID)
                .amount(DEFAULT_AMOUNT)
                .message(DEFAULT_MESSAGE)
                .success(TRUE_SUCCESS)
                .build();
    }

    public Map<String, Object> getChargeParams() {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (DEFAULT_AMOUNT.doubleValue() * 100));
        chargeParams.put("currency", DEFAULT_CURRENCY);
        chargeParams.put("source", DEFAULT_TOKEN_ID);
        return chargeParams;
    }

    public Map<String, Object> getChargeParamsWithParams(String token, String currency, String amount) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (new BigDecimal(amount).doubleValue() * 100));
        chargeParams.put("currency", currency);
        chargeParams.put("source", token);
        return chargeParams;
    }

    public Charge getCharge() {
        Charge charge = new Charge();
        charge.setId(DEFAULT_CHARGE_ID);
        charge.setPaid(true);
        charge.setAmount(DEFAULT_AMOUNT.longValue());
        Charge.Outcome outcome = new Charge.Outcome();
        outcome.setSellerMessage(DEFAULT_MESSAGE);
        charge.setOutcome(outcome);
        charge.setStatus(DEFAULT_MESSAGE);
        return charge;
    }

    public Charge getChargeWithChargeId(String chargeId) {
        Charge charge = getCharge();
        charge.setId(chargeId);
        return charge;
    }

    public Charge getChargeWithAmount(String amount) {
        Charge charge = getCharge();
        charge.setAmount(new BigDecimal(amount).longValue());
        return charge;
    }

    public CouponRequest getCouponRequest() {
        return CouponRequest.builder()
                .monthDuration(DEFAULT_MONTH_DURATION)
                .percent(DEFAULT_PERCENT)
                .build();
    }

    public CouponRequest getCouponRequestWithParameters(Long monthDuration, String percent) {
        return CouponRequest.builder()
                .monthDuration(monthDuration)
                .percent(new BigDecimal(percent))
                .build();
    }

    public CouponResponse getCouponResponse() {
        return CouponResponse.builder()
                .id(DEFAULT_COUPON_ID)
                .monthDuration(DEFAULT_MONTH_DURATION)
                .percent(DEFAULT_PERCENT)
                .build();
    }

    public Coupon getCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(DEFAULT_COUPON_ID);
        coupon.setDurationInMonths(DEFAULT_MONTH_DURATION);
        coupon.setPercentOff(DEFAULT_PERCENT);
        return coupon;
    }

    public Coupon getCouponWithId(String couponId) {
        Coupon coupon = getCoupon();
        coupon.setId(couponId);
        return coupon;
    }

    public Coupon getCouponWithParameters(Long monthDuration, String percent) {
        Coupon coupon = new Coupon();
        coupon.setId(DEFAULT_COUPON_ID);
        coupon.setDurationInMonths(monthDuration);
        coupon.setPercentOff(new BigDecimal(percent));
        return coupon;
    }

    public CustomerCreationRequest getCustomerCreationRequest() {
        return CustomerCreationRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .amount(DEFAULT_CUSTOMER_AMOUNT)
                .build();
    }

    public CustomerCreationRequest getCustomerCreationRequestWithParameters(String username, String phone,
                                                                            UUID passengerId, String amount) {
        return CustomerCreationRequest.builder()
                .passengerId(passengerId)
                .username(username)
                .phone(phone)
                .amount(new BigDecimal(amount))
                .build();
    }

    public CustomerCreationResponse getCustomerCreationResponse() {
        return CustomerCreationResponse.builder()
                .id(DEFAULT_CUSTOMER_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(DEFAULT_CUSTOMER_ID);
        customer.setPhone(DEFAULT_PHONE);
        customer.setName(DEFAULT_USERNAME);
        customer.setBalance((long)DEFAULT_CUSTOMER_AMOUNT.doubleValue() * 100);
        return customer;
    }

    public Customer getCustomerWithZeroBalance() {
        Customer customer = getCustomer();
        customer.setBalance(0L);
        return customer;
    }

    public Customer getCustomerWithParameters(String username, String phone, String amount) {
        Customer customer = new Customer();
        customer.setId(DEFAULT_CUSTOMER_ID);
        customer.setPhone(phone);
        customer.setName(username);
        customer.setBalance((long)new BigDecimal(amount).doubleValue() * 100);
        return customer;
    }

    public CustomerChargeRequest getCustomerChargeRequest() {
        return CustomerChargeRequest.builder()
                .amount(DEFAULT_CHARGE_AMOUNT)
                .passengerId(DEFAULT_PASSENGER_ID)
                .currency(DEFAULT_CURRENCY)
                .build();
    }

    public CustomerChargeRequest getCustomerChargeRequestWithParameters(String amount,
                                                                        UUID passengerId, String currency) {
        return CustomerChargeRequest.builder()
                .amount(new BigDecimal(amount))
                .passengerId(passengerId)
                .currency(currency)
                .build();
    }

    public CustomerChargeRequest getInfluentialChargeRequest() {
        return CustomerChargeRequest.builder()
                .amount(INFLUENTIAL_CHARGE_AMOUNT)
                .passengerId(DEFAULT_PASSENGER_ID)
                .currency(DEFAULT_CURRENCY)
                .build();
    }

    public CustomerChargeResponse getCustomerChargeResponse() {
        return CustomerChargeResponse.builder()
                .id(DEFAULT_CHARGE_ID)
                .amount(DEFAULT_CHARGE_AMOUNT)
                .passengerId(DEFAULT_PASSENGER_ID)
                .currency(DEFAULT_CURRENCY)
                .success(TRUE_SUCCESS)
                .build();
    }

    public CustomerUser getCustomerUser() {
        return CustomerUser.builder()
                .customerId(DEFAULT_CUSTOMER_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .build();
    }

    public CustomerUser getCustomerUserWithPassengerId(UUID passengerId) {
        CustomerUser customerUser = getCustomerUser();
        customerUser.setPassengerId(passengerId);
        return customerUser;
    }

    public PaymentIntent getPaymentIntent() {
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setId(DEFAULT_CHARGE_ID);
        paymentIntent.setAmount((long)DEFAULT_CHARGE_AMOUNT.doubleValue() * 100);
        paymentIntent.setCustomer(DEFAULT_CUSTOMER_ID);
        PaymentIntent.AutomaticPaymentMethods automaticPaymentMethods = new PaymentIntent.AutomaticPaymentMethods();
        automaticPaymentMethods.setEnabled(true);
        automaticPaymentMethods.setAllowRedirects(DEFAULT_ALLOW_REDIRECTS);
        paymentIntent.setAutomaticPaymentMethods(automaticPaymentMethods);
        paymentIntent.setPaymentMethod(DEFAULT_PAYMENT_METHOD);
        paymentIntent.setStatus(DEFAULT_MESSAGE);
        paymentIntent.setCurrency(DEFAULT_CURRENCY);
        return paymentIntent;
    }

    public PaymentIntent getPaymentIntentWithId(String chargeId) {
        PaymentIntent paymentIntent = getPaymentIntent();
        paymentIntent.setId(chargeId);
        return paymentIntent;
    }

    public PaymentIntent getPaymentIntentWithParameters(String amount, String currency) {
        PaymentIntent paymentIntent = getPaymentIntent();
        paymentIntent.setAmount((long)new BigDecimal(amount).doubleValue() * 100);
        paymentIntent.setCurrency(currency);
        return paymentIntent;
    }

    public CustomerCalculateRideResponse getCustomerCalculateRideWithCouponResponse() {
        return CustomerCalculateRideResponse.builder()
                .rideLength(DEFAULT_LENGTH)
                .rideDateTime(DEFAULT_DATETIME)
                .coupon(DEFAULT_COUPON_ID)
                .price(RIDE_PRICE_WITH_COUPON)
                .build();
    }

    public CustomerCalculateRideResponse getCustomerCalculateRideWithoutCouponResponse() {
        return CustomerCalculateRideResponse.builder()
                .rideLength(DEFAULT_LENGTH)
                .rideDateTime(DEFAULT_DATETIME)
                .price(RIDE_PRICE_WITHOUT_COUPON)
                .build();
    }

    public Refund getRefund() {
        Refund refund = new Refund();
        refund.setId(DEFAULT_REFUND_ID);
        refund.setAmount((long)DEFAULT_AMOUNT.doubleValue() * 100);
        refund.setCurrency(DEFAULT_CURRENCY);
        refund.setPaymentIntent(DEFAULT_CHARGE_ID);
        return refund;
    }

    public CustomerChargeReturnResponse getCustomerChargeReturnResponse() {
        return CustomerChargeReturnResponse.builder()
                .id(DEFAULT_REFUND_ID)
                .amount(DEFAULT_AMOUNT)
                .currency(DEFAULT_CURRENCY)
                .paymentId(DEFAULT_CHARGE_ID)
                .build();
    }

    public CustomerExistsResponse getCustomerExistsResponse() {
        return CustomerExistsResponse.builder()
                .isExists(CUSTOMER_EXISTS)
                .build();
    }

    public CustomerExistsResponse getCustomerNotExistsResponse() {
        return CustomerExistsResponse.builder()
                .isExists(CUSTOMER_NOT_EXISTS)
                .build();
    }
}
