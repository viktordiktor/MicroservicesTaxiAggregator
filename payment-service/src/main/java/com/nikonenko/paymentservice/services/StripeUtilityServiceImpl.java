package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.exceptions.ExpiredCouponException;
import com.nikonenko.paymentservice.exceptions.RetrieveCouponFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeUtilityServiceImpl implements StripeUtilityService {

    @Override
    public RequestOptions getRequestOptions(String key) {
        return RequestOptions.builder()
                .setApiKey(key)
                .build();
    }

    @Override
    public Coupon retrieveCoupon(String couponId, RequestOptions requestOptions) {
        try {
            Coupon coupon = Coupon.retrieve(couponId, requestOptions);
            checkCouponActive(coupon);
            return coupon;
        } catch (StripeException ex) {
            throw new RetrieveCouponFailedException(ex.getMessage());
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
