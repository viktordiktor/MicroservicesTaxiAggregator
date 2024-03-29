package com.nikonenko.rideservice.services.feign.impl;

import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeResponse;
import com.nikonenko.rideservice.dto.feign.payments.CustomerChargeReturnResponse;
import com.nikonenko.rideservice.feign.PaymentFeignClient;
import com.nikonenko.rideservice.services.feign.PaymentService;
import com.nikonenko.rideservice.utils.LogList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Retry(name = "paymentRetry")
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentFeignClient paymentFeignClient;

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackReturnChargePaymentService")
    public CustomerChargeReturnResponse returnCharge(String chargeId) {
        return paymentFeignClient.returnCharge(chargeId);
    }

    @Override
    @CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackGetChargeByIdPaymentService")
    public CustomerChargeResponse getChargeById(String chargeId) {
        return paymentFeignClient.getChargeById(chargeId);
    }

    public CustomerChargeReturnResponse fallbackReturnChargePaymentService(String chargeId, Exception ex) {
        log.error(LogList.LOG_RETURN_CHARGE_FEIGN_ERROR, chargeId, ex.getMessage());
        return CustomerChargeReturnResponse.builder()
                .id("")
                .paymentId("")
                .amount(BigDecimal.ZERO)
                .currency("")
                .errorMessage(ex.getMessage())
                .build();
    }

    public CustomerChargeResponse fallbackGetChargeByIdPaymentService(String chargeId, Exception ex) {
        log.error(LogList.LOG_GET_CHARGE_BY_ID_FEIGN_ERROR, chargeId, ex.getMessage());
        return CustomerChargeResponse.builder()
                .id("")
                .amount(BigDecimal.ZERO)
                .passengerId(UUID.randomUUID())
                .currency("")
                .success(false)
                .errorMessage(ex.getMessage())
                .build();
    }
}
