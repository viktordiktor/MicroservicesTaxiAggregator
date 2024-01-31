package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerRequest;
import com.nikonenko.paymentservice.dto.customers.StripeCustomerResponse;
import com.nikonenko.paymentservice.services.StripeCustomerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/stripe/customer")
public class StripeCustomerController {
    private final StripeCustomerServiceImpl stripeCustomerService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public StripeCustomerResponse createCustomer(@RequestBody @Valid StripeCustomerRequest customerRequest){
        return stripeCustomerService.createCustomer(customerRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public StripeCustomerChargeResponse customerCharge(@RequestBody @Valid StripeCustomerChargeRequest request) {
        return stripeCustomerService.customerCharge(request);
    }
}
