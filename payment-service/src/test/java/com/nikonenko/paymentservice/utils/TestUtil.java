package com.nikonenko.paymentservice.utils;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Token;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
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
    public final BigDecimal DEFAULT_PERCENT = BigDecimal.ONE;
    public final String DEFAULT_COUPON_ID = "coupon1";

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
        charge.setStatus("succeeded");
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
}
