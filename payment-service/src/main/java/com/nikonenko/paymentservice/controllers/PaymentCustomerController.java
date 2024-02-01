package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.customers.CustomerRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerRideResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerResponse;
import com.nikonenko.paymentservice.services.PaymentCustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/stripe/customer")
public class PaymentCustomerController {
    private final PaymentCustomerService paymentCustomerService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public StripeCustomerResponse createCustomer(@RequestBody @Valid StripeCustomerRequest customerRequest) {
        return paymentCustomerService.createCustomer(customerRequest);
    }

    @GetMapping("/ride-price")
    @ResponseStatus(HttpStatus.OK)
    public CustomerRideResponse calculateRidePrice(@RequestBody @Valid CustomerRideRequest customerRideRequest) {
        return paymentCustomerService.calculateRidePrice(customerRideRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public StripeCustomerChargeResponse customerCharge(@RequestBody @Valid StripeCustomerChargeRequest request) {
        return paymentCustomerService.customerCharge(request);
    }
}
