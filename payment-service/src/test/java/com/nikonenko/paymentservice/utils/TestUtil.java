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
    public final Long DEFAULT_PASSENGER_ID = 1L;
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

    public TokenResponse getTokenResponse() {
        return TokenResponse.builder()
                .token(DEFAULT_TOKEN)
                .cardNumber(DEFAULT_CARD_NUMBER)
                .build();
    }

    public Map<String, Object> getCardParams() {
        return  Map.of(
                "number", DEFAULT_CARD_NUMBER,
                "exp_month", DEFAULT_EXP_MONTH,
                "exp_year" , DEFAULT_EXP_YEAR,
                "cvc", DEFAULT_CVC
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

    public ChargeResponse getInitialChargeResponse() {
        return ChargeResponse.builder()
                .chargeId(DEFAULT_CHARGE_ID)
                .amount(DEFAULT_AMOUNT)
                .build();
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

    public CouponRequest getCouponRequest() {
        return CouponRequest.builder()
                .monthDuration(DEFAULT_MONTH_DURATION)
                .percent(DEFAULT_PERCENT)
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

    public CustomerCreationRequest getCustomerCreationRequest() {
        return CustomerCreationRequest.builder()
                .passengerId(DEFAULT_PASSENGER_ID)
                .username(DEFAULT_USERNAME)
                .phone(DEFAULT_PHONE)
                .amount(DEFAULT_CUSTOMER_AMOUNT)
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

    public CustomerChargeRequest getCustomerChargeRequest() {
        return CustomerChargeRequest.builder()
                .amount(DEFAULT_CHARGE_AMOUNT)
                .passengerId(DEFAULT_PASSENGER_ID)
                .currency(DEFAULT_CURRENCY)
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
