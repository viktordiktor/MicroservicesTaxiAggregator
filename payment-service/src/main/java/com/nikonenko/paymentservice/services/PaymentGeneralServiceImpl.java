package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.StripeBalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeCouponRequest;
import com.nikonenko.paymentservice.dto.StripeCouponResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Token;
import com.stripe.param.CouponCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentGeneralServiceImpl implements PaymentGeneralService {
    private final ModelMapper modelMapper;
    private final StripeUtilityService utilityService;

    @Override
    public StripeTokenResponse generateTokenByCard(StripeCardRequest stripeCardRequest) {
        Map<String, Object> cardParams = utilityService.createCardParams(stripeCardRequest);
        Token token = utilityService.stripeCreateToken(cardParams);
        return StripeTokenResponse.builder()
                .token(token.getId())
                .cardNumber(stripeCardRequest.getCardNumber())
                .build();
    }

    @Override
    public StripeChargeResponse charge(StripeChargeRequest chargeRequest) {
        StripeChargeResponse chargeResponse = modelMapper.map(chargeRequest, StripeChargeResponse.class);
        chargeResponse.setSuccess(false);
        Map<String, Object> chargeParams = utilityService.createChargeParams(chargeRequest);
        Charge charge = utilityService.stripeChargeCreation(chargeParams);
        utilityService.setChargeResponse(charge, chargeResponse);
        return chargeResponse;
    }

    @Override
    public StripeCouponResponse createCoupon(StripeCouponRequest stripeCouponRequest) {
        CouponCreateParams params =
                CouponCreateParams.builder()
                        .setDuration(CouponCreateParams.Duration.REPEATING)
                        .setDurationInMonths(stripeCouponRequest.getMonthDuration())
                        .setPercentOff(stripeCouponRequest.getPercent())
                        .build();
        Coupon coupon = utilityService.stripeCouponCreation(params);
        return StripeCouponResponse.builder()
                .id(coupon.getId())
                .monthDuration(coupon.getDurationInMonths())
                .percent(coupon.getPercentOff())
                .build();
    }

    @Override
    public StripeBalanceResponse getBalance() {
        Balance balance = utilityService.stripeRetrieveBalance();
        return StripeBalanceResponse
                .builder()
                .amount((double)balance.getPending().get(0).getAmount() / 100)
                .currency(balance.getPending().get(0).getCurrency())
                .build();
    }
}
