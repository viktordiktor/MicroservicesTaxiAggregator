package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.CustomerUpdateParams;

import java.time.LocalDateTime;
import java.util.Map;

public interface StripeUtilityService {
    Map<String, Object> createCardParams(CardRequest cardRequest);

    Token stripeCreateToken(Map<String, Object> cardParams);

    Map<String, Object> createChargeParams(ChargeRequest chargeRequest);

    Charge stripeChargeCreation(Map<String, Object> chargeParams);
    Charge stripeReceivingCharge(String chargeId);

    void setChargeResponse(Charge charge, ChargeResponse chargeResponse);

    Coupon stripeCouponCreation(CouponCreateParams params);

    RequestOptions getRequestOptions(String key);

    Coupon retrieveCoupon(String couponId, LocalDateTime localDateTime);

    Customer stripeCustomerCreation(CustomerCreationRequest customerRequest);

    void stripePaymentCreating(String customerId);

    Customer stripeCustomerRetrieving(String customerId);

    PaymentIntent stripeIntentConfirming(CustomerChargeRequest request, String customerId);

    PaymentIntent stripeIntentCreation(CustomerChargeRequest request, String customerId);

    void stripeCustomerUpdating(Customer customer, CustomerUpdateParams params);
}
