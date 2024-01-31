package com.nikonenko.paymentservice.services;

import com.stripe.model.Coupon;
import com.stripe.net.RequestOptions;

public interface StripeUtilityService {
    RequestOptions getRequestOptions(String key);

    Coupon retrieveCoupon(String couponId, RequestOptions requestOptions); //using in feature in ride price calc
}
