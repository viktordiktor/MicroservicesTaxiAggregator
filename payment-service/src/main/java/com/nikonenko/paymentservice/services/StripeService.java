package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.StripeBalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeCouponRequest;
import com.nikonenko.paymentservice.dto.StripeCouponResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;

public interface StripeService {
    StripeTokenResponse generateTokenByCard(StripeCardRequest stripeCardRequest);

    StripeChargeResponse charge(StripeChargeRequest stripeChargeRequest);

    StripeCouponResponse createCoupon(StripeCouponRequest stripeCouponRequest);

    StripeBalanceResponse getBalance();
}
