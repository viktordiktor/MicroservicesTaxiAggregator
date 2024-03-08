package com.nikonenko.paymentservice.utils;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.exceptions.RefundFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveChargeFailedException;
import com.nikonenko.paymentservice.exceptions.ConfirmIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreateChargeFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCouponFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCustomerException;
import com.nikonenko.paymentservice.exceptions.CreateIntentFailedException;
import com.nikonenko.paymentservice.exceptions.CreatePaymentFailedException;
import com.nikonenko.paymentservice.exceptions.CreateTokenFailedException;
import com.nikonenko.paymentservice.exceptions.ExpiredCouponException;
import com.nikonenko.paymentservice.exceptions.RetrieveCouponFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveCustomerFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveIntentFailedException;
import com.nikonenko.paymentservice.exceptions.UpdateCustomerFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Component
@RequiredArgsConstructor
public class StripeUtil {
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    @Value("${api.stripe.public-key}")
    private String publicKey;

    public Map<String, Object> createCardParams(CardRequest cardRequest) {
        return  Map.of(
                "number", cardRequest.getCardNumber(),
                "exp_month", cardRequest.getExpirationMonth(),
                "exp_year" , cardRequest.getExpirationYear(),
                "cvc", cardRequest.getCvc()
        );
    }

    public Token stripeCreateToken(Map<String, Object> cardParams) {
        try {
            Token token = Token.create(Map.of("card", cardParams),
                    getRequestOptions(publicKey));
            log.info(LogList.LOG_CREATE_TOKEN, token.getId());
            return token;
        } catch (StripeException ex) {
            throw new CreateTokenFailedException(ex.getMessage());
        }
    }

    public Map<String, Object> createChargeParams(ChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (chargeRequest.getAmount().doubleValue() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return chargeParams;
    }

    public Charge stripeChargeCreation(Map<String, Object> chargeParams) {
        try {
            Charge charge = Charge.create(chargeParams, getRequestOptions(secretKey));
            log.info(LogList.LOG_CREATE_CHARGE, charge.getId());
            return charge;
        } catch (StripeException ex) {
            throw new CreateChargeFailedException(ex.getMessage());
        }
    }

    public Charge stripeReceivingCharge(String chargeId) {
        try {
            Charge charge = Charge.retrieve(chargeId, getRequestOptions(secretKey));
            log.info(LogList.LOG_RETRIEVE_CHARGE, chargeId);
            return charge;
        } catch (StripeException ex) {
            throw new RetrieveChargeFailedException(ex.getMessage());
        }
    }

    public void setChargeResponse(Charge charge, ChargeResponse chargeResponse) {
        chargeResponse.setMessage(charge.getOutcome().getSellerMessage());
        if (charge.getPaid()) {
            chargeResponse.setChargeId(charge.getId());
            chargeResponse.setSuccess(true);
        }
    }

    public Coupon stripeCouponCreation(CouponCreateParams params) {
        try {
            Coupon coupon = Coupon.create(params, getRequestOptions(secretKey));
            log.info(LogList.LOG_CREATE_COUPON, coupon.getId());
            return coupon;
        } catch (StripeException ex) {
            throw new CreateCouponFailedException(ex.getMessage());
        }
    }

    public RequestOptions getRequestOptions(String key) {
        return RequestOptions.builder()
                .setApiKey(key)
                .build();
    }

    public Coupon retrieveCoupon(String couponId, LocalDateTime localDateTime) {
        try {
            Coupon coupon = Coupon.retrieve(couponId, getRequestOptions(secretKey));
            log.info(LogList.LOG_RETRIEVE_COUPON, couponId);
            checkCouponActive(coupon, localDateTime);
            return coupon;
        } catch (StripeException ex) {
            throw new RetrieveCouponFailedException(ex.getMessage());
        }
    }

    public Customer stripeCustomerCreation(CustomerCreationRequest customerRequest) {
        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setPhone(customerRequest.getPhone())
                    .setName(customerRequest.getUsername())
                    .setBalance((long) customerRequest.getAmount().doubleValue() * 100)
                    .build();
            Customer customer = Customer.create(customerCreateParams, getRequestOptions(secretKey));
            log.info(LogList.LOG_CREATE_CUSTOMER, customer.getId());
            return customer;
        } catch (StripeException ex) {
            throw new CreateCustomerException(ex.getMessage());
        }
    }

    public void stripePaymentCreating(String customerId)  {
        try{
            Map<String, Object> paymentParams = Map.of(
                    "type", "card",
                    "card", Map.of("token", "tok_visa")
            );
            PaymentMethod paymentMethod = PaymentMethod.create(paymentParams,
                    getRequestOptions(secretKey));
            paymentMethod.attach(Map.of("customer", customerId), getRequestOptions(secretKey));
            log.info(LogList.LOG_CREATE_PAYMENT_METHOD, paymentMethod.getId());
        } catch (StripeException ex){
            throw new CreatePaymentFailedException(ex.getMessage());
        }
    }

    public Customer stripeCustomerRetrieving(String customerId) {
        try {
            Customer customer = Customer.retrieve(customerId, getRequestOptions(secretKey));
            log.info(LogList.LOG_RETRIEVE_CUSTOMER, customerId);
            return customer;
        } catch (StripeException ex) {
            throw new RetrieveCustomerFailedException(ex.getMessage());
        }
    }

    public PaymentIntent stripeIntentConfirming(CustomerChargeRequest request, String customerId) {
        PaymentIntent intent = stripeIntentCreation(request, customerId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa")
                .build();
        try {
            PaymentIntent confirmedIntent = intent.confirm(params, getRequestOptions(secretKey));
            log.info(LogList.LOG_CONFIRM_INTENT, confirmedIntent.getId());
            return confirmedIntent;
        } catch (StripeException ex) {
            throw new ConfirmIntentFailedException(ex.getMessage());
        }
    }

    public PaymentIntent stripeIntentCreation(CustomerChargeRequest request, String customerId) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of("amount",
                            (long) (request.getAmount().doubleValue() * 100),
                            "currency", request.getCurrency(),
                            "customer", customerId,
                            "automatic_payment_methods", createAutomaticPaymentMethods()),
                    getRequestOptions(secretKey));
            intent.setPaymentMethod(customerId);
            log.info(LogList.LOG_CREATE_INTENT, intent.getId());
            return intent;
        } catch (StripeException ex) {
            throw new CreateIntentFailedException(ex.getMessage());
        }
    }

    public Refund stripeRefund(String intentId) {
        try {
            RefundCreateParams params =
                    RefundCreateParams.builder().setPaymentIntent(intentId).build();
            return Refund.create(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new RefundFailedException(ex.getMessage());
        }
    }

    public PaymentIntent stripeIntentRetrieving(String intentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(intentId, getRequestOptions(secretKey));
            log.info(LogList.LOG_RETRIEVE_INTENT, paymentIntent.getId());
            return paymentIntent;
        } catch (StripeException ex) {
            throw new RetrieveIntentFailedException(ex.getMessage());
        }
    }

    private Map<String, Object> createAutomaticPaymentMethods() {
        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        automaticPaymentMethods.put("allow_redirects", "never");
        return automaticPaymentMethods;
    }

    public void stripeCustomerUpdating(Customer customer, CustomerUpdateParams params) {
        try {
            customer.update(params, getRequestOptions(secretKey));
            log.info(LogList.LOG_UPDATE_CUSTOMER, customer.getId());
        } catch (StripeException ex) {
            throw new UpdateCustomerFailedException(ex.getMessage());
        }
    }

    private void checkCouponActive(Coupon coupon, LocalDateTime requestDateTime) {
        LocalDateTime couponExpiration =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(coupon.getCreated()),
                        TimeZone.getDefault().toZoneId())
                        .plusMonths(coupon.getDurationInMonths());
        log.info(LogList.LOG_COUPON_EXP_DATE, couponExpiration, requestDateTime);
        if (couponExpiration.isBefore(requestDateTime)) {
            throw new ExpiredCouponException();
        }
    }
}
