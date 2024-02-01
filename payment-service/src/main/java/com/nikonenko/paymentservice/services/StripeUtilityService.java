package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.CustomerUpdateParams;

import java.util.Map;

public interface StripeUtilityService {
    Map<String, Object> createCardParams(StripeCardRequest stripeCardRequest);

    Token stripeCreateToken(Map<String, Object> cardParams);

    Map<String, Object> createChargeParams(StripeChargeRequest chargeRequest);

    Charge stripeChargeCreation(Map<String, Object> chargeParams);

    void setChargeResponse(Charge charge, StripeChargeResponse chargeResponse);

    Coupon stripeCouponCreation(CouponCreateParams params);

    Balance stripeRetrieveBalance();

    RequestOptions getRequestOptions(String key);

    Coupon retrieveCoupon(String couponId); //using in feature in ride price calc

    Customer stripeCustomerCreation(StripeCustomerRequest customerRequest);

    void stripePaymentCreating(String customerId);

    Customer stripeCustomerRetrieving(String customerId);

    PaymentIntent stripeIntentConfirming(StripeCustomerChargeRequest request, String customerId);

    PaymentIntent stripeIntentCreation(StripeCustomerChargeRequest request, String customerId);

    void stripeCustomerUpdating(Customer customer, CustomerUpdateParams params);
}
