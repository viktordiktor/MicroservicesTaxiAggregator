package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.customers.CustomerRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
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
@RequestMapping("api/v1/payments/customers")
public class PaymentCustomerController {
    private final PaymentCustomerService paymentCustomerService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerCreationResponse createCustomer(@RequestBody @Valid CustomerCreationRequest customerRequest) {
        return paymentCustomerService.createCustomer(customerRequest);
    }

    @GetMapping("/ride-price")
    @ResponseStatus(HttpStatus.OK)
    public CustomerRideResponse calculateRidePrice(@RequestBody @Valid CustomerRideRequest customerRideRequest) {
        return paymentCustomerService.calculateRidePrice(customerRideRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public CustomerChargeResponse customerCharge(@RequestBody @Valid CustomerChargeRequest customerChargeRequest) {
        return paymentCustomerService.customerCharge(customerChargeRequest);
    }
}
