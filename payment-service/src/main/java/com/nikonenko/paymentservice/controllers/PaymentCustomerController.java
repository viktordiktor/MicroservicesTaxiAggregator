package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerCreationResponse;
import com.nikonenko.paymentservice.services.PaymentCustomerService;
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
    public CustomerCalculateRideResponse calculateRidePrice(@RequestBody @Valid CustomerCalculateRideRequest customerCalculateRideRequest) {
        return paymentCustomerService.calculateRidePrice(customerCalculateRideRequest);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerChargeResponse customerCharge(@RequestBody @Valid CustomerChargeRequest customerChargeRequest) {
        return paymentCustomerService.customerCharge(customerChargeRequest);
    }

    @PostMapping("/charge/{chargeId}/return")
    public void returnCustomerCharge(@PathVariable String chargeId) {
        paymentCustomerService.returnCustomerCharge(chargeId);
    }
}
