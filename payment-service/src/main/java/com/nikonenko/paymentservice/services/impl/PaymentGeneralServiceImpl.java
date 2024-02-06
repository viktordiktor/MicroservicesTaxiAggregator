package com.nikonenko.paymentservice.services.impl;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.services.PaymentGeneralService;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Token;
import com.stripe.param.CouponCreateParams;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentGeneralServiceImpl implements PaymentGeneralService {
    private final ModelMapper modelMapper;
    private final StripeUtil stripeUtil;

    @Override
    public TokenResponse generateTokenByCard(CardRequest cardRequest) {
        Map<String, Object> cardParams = stripeUtil.createCardParams(cardRequest);
        Token token = stripeUtil.stripeCreateToken(cardParams);
        return TokenResponse.builder()
                .token(token.getId())
                .cardNumber(cardRequest.getCardNumber())
                .build();
    }

    @Override
    public ChargeResponse charge(ChargeRequest chargeRequest) {
        ChargeResponse chargeResponse = modelMapper.map(chargeRequest, ChargeResponse.class);
        chargeResponse.setSuccess(false);
        Map<String, Object> chargeParams = stripeUtil.createChargeParams(chargeRequest);
        Charge charge = stripeUtil.stripeChargeCreation(chargeParams);
        stripeUtil.setChargeResponse(charge, chargeResponse);
        return chargeResponse;
    }

    @Override
    public ChargeResponse getChargeById(String chargeId) {
        Charge charge = stripeUtil.stripeReceivingCharge(chargeId);
        return modelMapper.map(charge, ChargeResponse.class);
    }

    @Override
    public CouponResponse createCoupon(CouponRequest couponRequest) {
        CouponCreateParams params =
                CouponCreateParams.builder()
                        .setDuration(CouponCreateParams.Duration.REPEATING)
                        .setDurationInMonths(couponRequest.getMonthDuration())
                        .setPercentOff(couponRequest.getPercent())
                        .build();
        Coupon coupon = stripeUtil.stripeCouponCreation(params);
        return CouponResponse.builder()
                .id(coupon.getId())
                .monthDuration(coupon.getDurationInMonths())
                .percent(coupon.getPercentOff())
                .build();
    }
}