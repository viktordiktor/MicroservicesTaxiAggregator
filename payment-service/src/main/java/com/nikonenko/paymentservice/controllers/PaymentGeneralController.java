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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/general")
public class PaymentGeneralController {
    private final PaymentGeneralService paymentGeneralService;

    @PostMapping("/token")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse generateTokenByCard(@RequestBody @Valid CardRequest cardRequest) {
        return paymentGeneralService.generateTokenByCard(cardRequest);
    }

    @PostMapping("/charge")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ChargeResponse charge(@RequestBody @Valid ChargeRequest chargeRequest) {
        return paymentGeneralService.charge(chargeRequest);
    }

    @GetMapping("/charge/{chargeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ChargeResponse getChargeById(@PathVariable String chargeId) {
        return paymentGeneralService.getChargeById(chargeId);
    }

    @PostMapping("/coupon")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CouponResponse createCoupon(@RequestBody @Valid CouponRequest couponRequest) {
        return paymentGeneralService.createCoupon(couponRequest);
    }
}