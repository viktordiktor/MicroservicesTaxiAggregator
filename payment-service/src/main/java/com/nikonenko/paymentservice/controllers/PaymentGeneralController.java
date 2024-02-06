package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.CardRequest;
import com.nikonenko.paymentservice.dto.ChargeRequest;
import com.nikonenko.paymentservice.dto.ChargeResponse;
import com.nikonenko.paymentservice.dto.CouponRequest;
import com.nikonenko.paymentservice.dto.CouponResponse;
import com.nikonenko.paymentservice.dto.TokenResponse;
import com.nikonenko.paymentservice.services.PaymentGeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/general")
@RestControllerAdvice
public class PaymentGeneralController {
    private final PaymentGeneralService paymentGeneralService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse generateTokenByCard(@RequestBody @Valid CardRequest cardRequest) {
        return paymentGeneralService.generateTokenByCard(cardRequest);
    }

    @PostMapping("/charge")
    public ChargeResponse charge(@RequestBody @Valid ChargeRequest chargeRequest) {
        return paymentGeneralService.charge(chargeRequest);
    }

    @GetMapping("/charge/{chargeId}")
    public ChargeResponse getChargeById(@PathVariable String chargeId) {
        return paymentGeneralService.getChargeById(chargeId);
    }

    @PostMapping("/coupon")
    @ResponseStatus(HttpStatus.CREATED)
    public CouponResponse createCoupon(@RequestBody @Valid CouponRequest couponRequest) {
        return paymentGeneralService.createCoupon(couponRequest);
    }
}