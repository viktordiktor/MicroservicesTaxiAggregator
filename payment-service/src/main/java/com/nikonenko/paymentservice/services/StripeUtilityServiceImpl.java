package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.exceptions.ConfirmIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreateChargeFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCouponFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCustomerException;
import com.nikonenko.paymentservice.exceptions.CreateIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreatePaymentFailedException;
import com.nikonenko.paymentservice.exceptions.CreateTokenFailedException;
import com.nikonenko.paymentservice.exceptions.ExpiredCouponException;
import com.nikonenko.paymentservice.exceptions.RetrieveBalanceFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveCouponFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveCustomerFailedException;
import com.nikonenko.paymentservice.exceptions.UpdateCustomerFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeUtilityServiceImpl implements StripeUtilityService {
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    @Value("${api.stripe.public-key}")
    private String publicKey;

    @Override
    public Map<String, Object> createCardParams(CardRequest cardRequest) {
        return  Map.of(
                "number", cardRequest.getCardNumber(),
                "exp_month", cardRequest.getExpirationMonth(),
                "exp_year" , cardRequest.getExpirationYear(),
                "cvc", cardRequest.getCvc()
        );
    }

    @Override
    public Token stripeCreateToken(Map<String, Object> cardParams) {
        try {
            log.info("Creating new token..");
            return Token.create(Map.of("card", cardParams),
                    getRequestOptions(publicKey));
        } catch (StripeException ex) {
            throw new CreateTokenFailedException(ex.getMessage());
        }
    }

    @Override
    public Map<String, Object> createChargeParams(ChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (chargeRequest.getAmount() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return chargeParams;
    }

    @Override
    public Charge stripeChargeCreation(Map<String, Object> chargeParams) {
        try {
            log.info("Creating new charge..");
            return Charge.create(chargeParams, getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new CreateChargeFailedException(e.getMessage());
        }
    }

    @Override
    public void setChargeResponse(Charge charge, ChargeResponse chargeResponse) {
        chargeResponse.setMessage(charge.getOutcome().getSellerMessage());
        if (charge.getPaid()) {
            chargeResponse.setChargeId(charge.getId());
            chargeResponse.setSuccess(true);
        }
    }

    @Override
    public Coupon stripeCouponCreation(CouponCreateParams params) {
        try {
            log.info("Creating coupon..");
            return Coupon.create(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCouponFailedException(ex.getMessage());
        }
    }

    @Override
    public Balance stripeRetrieveBalance() {
        try {
            log.info("Retrieving balance..");
            return Balance.retrieve(getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new RetrieveBalanceFailedException(e.getMessage());
        }
    }

    @Override
    public RequestOptions getRequestOptions(String key) {
        return RequestOptions.builder()
                .setApiKey(key)
                .build();
    }

    @Override
    public Coupon retrieveCoupon(String couponId, LocalDateTime localDateTime) {
        try {
            log.info("Retrieving coupon with id: {}", couponId);
            Coupon coupon = Coupon.retrieve(couponId, getRequestOptions(secretKey));
            checkCouponActive(coupon, localDateTime);
            return coupon;
        } catch (StripeException ex) {
            throw new RetrieveCouponFailedException(ex.getMessage());
        }
    }

    @Override
    public Customer stripeCustomerCreation(CustomerCreationRequest customerRequest) {
        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setPhone(customerRequest.getPhone())
                    .setName(customerRequest.getUsername())
                    .setBalance(customerRequest.getAmount())
                    .build();
            log.info("Creating new customer..");
            return Customer.create(customerCreateParams, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCustomerException(ex.getMessage());
        }
    }

    @Override
    public void stripePaymentCreating(String customerId)  {
        try{
            Map<String, Object> paymentParams = Map.of(
                    "type", "card",
                    "card", Map.of("token", "tok_visa")
            );
            log.info("Creating new payment..");
            PaymentMethod paymentMethod = PaymentMethod.create(paymentParams,
                    getRequestOptions(secretKey));
            log.info("Attaching customer to payment with id: {}", paymentMethod.getId());
            paymentMethod.attach(Map.of("customer", customerId), getRequestOptions(secretKey));
        } catch (StripeException ex){
            throw new CreatePaymentFailedException(ex.getMessage());
        }
    }

    @Override
    public Customer stripeCustomerRetrieving(String customerId) {
        try {
            log.info("Retrieving customer with id: {}", customerId);
            return Customer.retrieve(customerId, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new RetrieveCustomerFailedException(ex.getMessage());
        }
    }

    @Override
    public PaymentIntent stripeIntentConfirming(CustomerChargeRequest request, String customerId) {
        PaymentIntent intent = stripeIntentCreation(request, customerId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa")
                .build();
        try {
            log.info("Confirming intent with id: {}", intent.getId());
            return intent.confirm(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new ConfirmIntentFailedException(ex.getMessage());
        }
    }

    @Override
    public PaymentIntent stripeIntentCreation(CustomerChargeRequest request, String customerId) {
        try {
            log.info("Creating new intent..");
            PaymentIntent intent = PaymentIntent.create(Map.of("amount", (int) (request.getAmount() * 100),
                            "currency", request.getCurrency(),
                            "customer", customerId,
                            "automatic_payment_methods", createAutomaticPaymentMethods()),
                    getRequestOptions(secretKey));
            intent.setPaymentMethod(customerId);
            return intent;
        } catch (StripeException ex) {
            throw new CreateIntentFailedException(ex.getMessage());
        }
    }

    private Map<String, Object> createAutomaticPaymentMethods() {
        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        automaticPaymentMethods.put("allow_redirects", "never");
        return automaticPaymentMethods;
    }

    @Override
    public void stripeCustomerUpdating(Customer customer, CustomerUpdateParams params) {
        try {
            log.info("Updating customer with id: {}", customer.getId());
            customer.update(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new UpdateCustomerFailedException(ex.getMessage());
        }
    }

    private void checkCouponActive(Coupon coupon, LocalDateTime requestDateTime) {
        LocalDateTime couponExpiration =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(coupon.getCreated()),
                        TimeZone.getDefault().toZoneId())
                        .plusMonths(coupon.getDurationInMonths());
        log.info("Coupon expiration date: {}\nRequest date: {}", couponExpiration, requestDateTime);
        if (couponExpiration.isBefore(requestDateTime)) {
            throw new ExpiredCouponException();
        }
    }
}
