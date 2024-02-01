package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeUtilityServiceImpl implements StripeUtilityService {
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    @Value("${api.stripe.public-key}")
    private String publicKey;

    @Override
    public Map<String, Object> createCardParams(StripeCardRequest stripeCardRequest) {
        return  Map.of(
                "number", stripeCardRequest.getCardNumber(),
                "exp_month", stripeCardRequest.getExpirationMonth(),
                "exp_year" , stripeCardRequest.getExpirationYear(),
                "cvc", stripeCardRequest.getCvc()
        );
    }

    @Override
    public Token stripeCreateToken(Map<String, Object> cardParams) {
        try {
            return Token.create(Map.of("card", cardParams),
                    getRequestOptions(publicKey));
        } catch (StripeException ex) {
            throw new CreateTokenFailedException(ex.getMessage());
        }
    }

    @Override
    public Map<String, Object> createChargeParams(StripeChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (chargeRequest.getAmount() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return chargeParams;
    }

    @Override
    public Charge stripeChargeCreation(Map<String, Object> chargeParams) {
        try {
            return Charge.create(chargeParams, getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new CreateChargeFailedException(e.getMessage());
        }
    }

    @Override
    public void setChargeResponse(Charge charge, StripeChargeResponse chargeResponse) {
        chargeResponse.setMessage(charge.getOutcome().getSellerMessage());
        if (charge.getPaid()) {
            chargeResponse.setChargeId(charge.getId());
            chargeResponse.setSuccess(true);
        }
    }

    @Override
    public Coupon stripeCouponCreation(CouponCreateParams params) {
        try {
            return Coupon.create(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCouponFailedException(ex.getMessage());
        }
    }

    @Override
    public Balance stripeRetrieveBalance() {
        try {
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
    public Coupon retrieveCoupon(String couponId) {
        try {
            Coupon coupon = Coupon.retrieve(couponId, getRequestOptions(secretKey));
            checkCouponActive(coupon);
            return coupon;
        } catch (StripeException ex) {
            throw new RetrieveCouponFailedException(ex.getMessage());
        }
    }

    @Override
    public Customer stripeCustomerCreation(StripeCustomerRequest customerRequest) {
        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setPhone(customerRequest.getPhone())
                    .setName(customerRequest.getUsername())
                    .setBalance(customerRequest.getAmount())
                    .build();
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
            PaymentMethod paymentMethod = PaymentMethod.create(paymentParams,
                    getRequestOptions(secretKey));
            paymentMethod.attach(Map.of("customer", customerId), getRequestOptions(secretKey));
        } catch (StripeException ex){
            throw new CreatePaymentFailedException(ex.getMessage());
        }
    }

    @Override
    public Customer stripeCustomerRetrieving(String customerId) {
        try {
            return Customer.retrieve(customerId, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new RetrieveCustomerFailedException(ex.getMessage());
        }
    }

    @Override
    public PaymentIntent stripeIntentConfirming(StripeCustomerChargeRequest request, String customerId) {
        PaymentIntent intent = stripeIntentCreation(request, customerId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa")
                .build();
        try {
            return intent.confirm(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new ConfirmIntentFailedException(ex.getMessage());
        }
    }

    @Override
    public PaymentIntent stripeIntentCreation(StripeCustomerChargeRequest request, String customerId) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of("amount", (int)(request.getAmount() * 100),
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
            customer.update(params, getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new UpdateCustomerFailedException(ex.getMessage());
        }
    }

    private void checkCouponActive(Coupon coupon) {
        Date couponExpiration = new Date((coupon.getCreated() + coupon.getDurationInMonths()) * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(couponExpiration);
        calendar.add(Calendar.MONTH, 4);
        couponExpiration = calendar.getTime();
        if (couponExpiration.before(new Date())) {
            throw new ExpiredCouponException();
        }
    }
}
