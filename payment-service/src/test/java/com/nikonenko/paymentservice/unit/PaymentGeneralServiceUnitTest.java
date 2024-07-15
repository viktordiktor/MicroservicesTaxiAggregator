package com.nikonenko.paymentservice.unit;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.services.impl.PaymentGeneralServiceImpl;
import com.nikonenko.paymentservice.utils.StripeUtil;
import com.nikonenko.paymentservice.utils.TestUtil;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Token;
import com.stripe.param.CouponCreateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentGeneralServiceUnitTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private StripeUtil stripeUtil;
    @InjectMocks
    private PaymentGeneralServiceImpl paymentGeneralService;

    @Test
    void givenCardRequest_whenGenerateToken_thenGenerateToken() {
        Map<String, Object> cardParams = TestUtil.getCardParams();
        Token token = TestUtil.getToken();
        CardRequest request = TestUtil.getCardRequest();
        TokenResponse response = TestUtil.getTokenResponse();

        doReturn(cardParams)
                .when(stripeUtil)
                .createCardParams(request);
        doReturn(token)
                .when(stripeUtil)
                .stripeCreateToken(cardParams);

        TokenResponse result = paymentGeneralService.generateTokenByCard(request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(stripeUtil).createCardParams(request);
        verify(stripeUtil).stripeCreateToken(cardParams);
    }

    @Test
    void givenChargeRequest_whenCreateCharge_thenCreateCharge() {
        ChargeRequest request = TestUtil.getChargeRequest();
        ChargeResponse initialResponse = TestUtil.getInitialChargeResponse();
        ChargeResponse response = TestUtil.getChargeResponse();
        Map<String, Object> chargeParams = TestUtil.getChargeParams();
        Charge charge = TestUtil.getCharge();

        doReturn(initialResponse)
                .when(modelMapper)
                .map(request, ChargeResponse.class);
        doReturn(chargeParams)
                .when(stripeUtil)
                .createChargeParams(request);
        doReturn(charge)
                .when(stripeUtil)
                .stripeChargeCreation(chargeParams);
        doCallRealMethod()
                .when(stripeUtil)
                .setChargeResponse(charge, initialResponse);
        ChargeResponse result = paymentGeneralService.charge(request);

        verify(modelMapper).map(request, ChargeResponse.class);
        verify(stripeUtil).createChargeParams(request);
        verify(stripeUtil).stripeChargeCreation(chargeParams);
        verify(stripeUtil).setChargeResponse(charge, initialResponse);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void givenChargeId_whenFindCharge_thenReturnCharge() {
        ChargeResponse response = TestUtil.getChargeResponse();
        Charge charge = TestUtil.getCharge();

        doReturn(charge)
                .when(stripeUtil)
                .stripeReceivingCharge(charge.getId());

        ChargeResponse result = paymentGeneralService.getChargeById(charge.getId());

        assertNotNull(result);
        assertEquals(response, result);
        verify(stripeUtil).stripeReceivingCharge(charge.getId());
    }

    @Test
    void givenCouponRequest_whenCreateCoupon_thenReturnCoupon() {
        CouponRequest request = TestUtil.getCouponRequest();
        CouponResponse response = TestUtil.getCouponResponse();
        Coupon coupon = TestUtil.getCoupon();

        doReturn(coupon)
                .when(stripeUtil)
                .stripeCouponCreation(any(CouponCreateParams.class));

        CouponResponse result = paymentGeneralService.createCoupon(request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(stripeUtil).stripeCouponCreation(any(CouponCreateParams.class));
    }
}
