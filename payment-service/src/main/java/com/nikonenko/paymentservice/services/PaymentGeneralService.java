package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;

public interface PaymentGeneralService {
    TokenResponse generateTokenByCard(CardRequest cardRequest);

    ChargeResponse charge(ChargeRequest chargeRequest);

    ChargeResponse getChargeById(String chargeId);

    CouponResponse createCoupon(CouponRequest couponRequest);
}
