package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.StripeBalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeCouponRequest;
import com.nikonenko.paymentservice.dto.StripeCouponResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;
import com.nikonenko.paymentservice.exceptions.CreateChargeFailedException;
import com.nikonenko.paymentservice.exceptions.CreateCouponFailedException;
import com.nikonenko.paymentservice.exceptions.CreateTokenFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveBalanceFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Token;
import com.stripe.param.CouponCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService{
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    @Value("${api.stripe.public-key}")
    private String publicKey;
    private final ModelMapper modelMapper;
    private final StripeUtilityService utilityService;

    @Override
    public StripeTokenResponse generateTokenByCard(StripeCardRequest stripeCardRequest) {
        Map<String, Object> cardParams = createCardParams(stripeCardRequest);
        Token token = stripeCreateToken(cardParams);
        return StripeTokenResponse.builder()
                .token(token.getId())
                .cardNumber(stripeCardRequest.getCardNumber())
                .build();
    }

    private Map<String, Object> createCardParams(StripeCardRequest stripeCardRequest) {
        return  Map.of(
                "number", stripeCardRequest.getCardNumber(),
                "exp_month", stripeCardRequest.getExpirationMonth(),
                "exp_year" , stripeCardRequest.getExpirationYear(),
                "cvc", stripeCardRequest.getCvc()
        );
    }

    private Token stripeCreateToken(Map<String, Object> cardParams) {
        try {
            return Token.create(Map.of("card", cardParams),
                    utilityService.getRequestOptions(publicKey));
        } catch (StripeException ex) {
            throw new CreateTokenFailedException(ex.getMessage());
        }
    }

    @Override
    public StripeChargeResponse charge(StripeChargeRequest chargeRequest) {
        StripeChargeResponse chargeResponse = modelMapper.map(chargeRequest, StripeChargeResponse.class);
        chargeResponse.setSuccess(false);
        Map<String, Object> chargeParams = createChargeParams(chargeRequest);
        Charge charge = stripeChargeCreation(chargeParams);
        setChargeResponse(charge, chargeResponse);
        return chargeResponse;
    }

    private Map<String, Object> createChargeParams(StripeChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (long) (chargeRequest.getAmount() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return chargeParams;
    }

    private Charge stripeChargeCreation(Map<String, Object> chargeParams) {
        try {
            return Charge.create(chargeParams, utilityService.getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new CreateChargeFailedException(e.getMessage());
        }
    }

    private void setChargeResponse(Charge charge, StripeChargeResponse chargeResponse) {
        chargeResponse.setMessage(charge.getOutcome().getSellerMessage());
        if (charge.getPaid()) {
            chargeResponse.setChargeId(charge.getId());
            chargeResponse.setSuccess(true);
        }
    }

    @Override
    public StripeCouponResponse createCoupon(StripeCouponRequest stripeCouponRequest) {
        CouponCreateParams params =
                CouponCreateParams.builder()
                        .setDuration(CouponCreateParams.Duration.REPEATING)
                        .setDurationInMonths(stripeCouponRequest.getMonthDuration())
                        .setPercentOff(stripeCouponRequest.getPercent())
                        .build();
        Coupon coupon = stripeCouponCreation(params);
        return StripeCouponResponse.builder()
                .id(coupon.getId())
                .monthDuration(coupon.getDurationInMonths())
                .percent(coupon.getPercentOff())
                .build();
    }

    private Coupon stripeCouponCreation(CouponCreateParams params) {
        try {
            return Coupon.create(params, utilityService.getRequestOptions(secretKey));
        } catch (StripeException ex) {
            throw new CreateCouponFailedException(ex.getMessage());
        }
    }

    @Override
    public StripeBalanceResponse getBalance() {
        Balance balance = stripeRetrieveBalance();
        return StripeBalanceResponse
                .builder()
                .amount((double)balance.getPending().get(0).getAmount() / 100)
                .currency(balance.getPending().get(0).getCurrency())
                .build();
    }

    private Balance stripeRetrieveBalance() {
        try {
            return Balance.retrieve(utilityService.getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new RetrieveBalanceFailedException(e.getMessage());
        }
    }
}
