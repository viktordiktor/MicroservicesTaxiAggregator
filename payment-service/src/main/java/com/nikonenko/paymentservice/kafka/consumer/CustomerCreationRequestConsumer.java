package com.nikonenko.paymentservice.kafka.consumer;

import com.nikonenko.paymentservice.dto.customers.CustomerCreationRequest;
import com.nikonenko.paymentservice.services.PaymentCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerCreationRequestConsumer {
    private final PaymentCustomerService paymentCustomerService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.payment-customer-name}")
    public void handleCustomerCreationRequest(CustomerCreationRequest request) {
        log.info("Receiver request for Creation customer for passenger: {}", request.getPassengerId());
        paymentCustomerService.createCustomer(request);
    }
}
