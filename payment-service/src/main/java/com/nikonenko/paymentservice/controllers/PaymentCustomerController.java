package com.nikonenko.paymentservice.controllers;

import com.nikonenko.paymentservice.dto.customers.CustomerCalculateRideResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeRequest;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerChargeReturnResponse;
import com.nikonenko.paymentservice.dto.customers.CustomerExistsResponse;
import com.nikonenko.paymentservice.services.PaymentCustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments/customers")
public class PaymentCustomerController {
    private final PaymentCustomerService paymentCustomerService;

    @GetMapping("/checkExists/{passengerId}")
    public CustomerExistsResponse isCustomerExists(@PathVariable Long passengerId) {
        return paymentCustomerService.isCustomerExists(passengerId);
    }

    @GetMapping("/ride-price")
    public CustomerCalculateRideResponse calculateRidePrice(@RequestParam(value = "rideLength") Double rideLength,
                                         @RequestParam(value = "rideDateTime") LocalDateTime rideDateTime,
                                         @RequestParam(value = "coupon", required = false) String coupon) {
        return paymentCustomerService.calculateRidePrice(rideLength, rideDateTime, coupon);
    }

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerChargeResponse createCustomerCharge(@RequestBody
                                                           @Valid CustomerChargeRequest customerChargeRequest) {
        return paymentCustomerService.createCustomerCharge(customerChargeRequest);
    }

    @GetMapping("/charge/{chargeId}")
    public CustomerChargeResponse getCustomerCharge(@PathVariable String chargeId) {
        return paymentCustomerService.getCustomerCharge(chargeId);
    }

    @PostMapping("/charge/{chargeId}/return")
    public CustomerChargeReturnResponse returnCustomerCharge(@PathVariable String chargeId) {
        return paymentCustomerService.returnCustomerCharge(chargeId);
    }
}
