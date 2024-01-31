package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.StripeBalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeCouponRequest;
import com.nikonenko.paymentservice.dto.StripeCouponResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;
import com.nikonenko.paymentservice.services.StripeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stripe")
@RestControllerAdvice
public class StripeController {
    private final StripeServiceImpl stripeService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public StripeTokenResponse generateTokenByCard(@RequestBody @Valid StripeCardRequest stripeCardRequest) {
        return stripeService.generateTokenByCard(stripeCardRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public StripeChargeResponse charge(@RequestBody @Valid StripeChargeRequest stripeChargeRequest) {
        return stripeService.charge(stripeChargeRequest);
    }

    @PostMapping("/coupon")
    @ResponseStatus(HttpStatus.OK)
    public StripeCouponResponse createCoupon(@RequestBody @Valid StripeCouponRequest stripeCouponRequest) {
        return stripeService.createCoupon(stripeCouponRequest);
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public StripeBalanceResponse getBalance() {
        return stripeService.getBalance();
    }

}