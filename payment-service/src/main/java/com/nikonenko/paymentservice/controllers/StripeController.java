package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.BalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;
import com.nikonenko.paymentservice.services.StripeServiceImpl;
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
@RequestMapping("/api/stripe")
@RestControllerAdvice
public class StripeController {
    private final StripeServiceImpl stripeService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public StripeTokenResponse generateTokenByCard(@RequestBody StripeCardRequest stripeCardRequest) {
        return stripeService.generateTokenByCard(stripeCardRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public StripeChargeResponse charge(@RequestBody StripeChargeRequest stripeChargeRequest) {
        return stripeService.charge(stripeChargeRequest);
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceResponse getBalance() {
        return stripeService.getBalance();
    }

}