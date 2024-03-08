package com.nikonenko.passengerservice.kafka.producer;

import com.nikonenko.passengerservice.dto.CustomerCreationRequest;
import com.nikonenko.passengerservice.utils.LogList;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerCreationRequestProducer {
    @Value("${spring.kafka.producer.payment-customer-topic.name}")
    private String customerTopic;
    private final KafkaTemplate<String, CustomerCreationRequest> kafkaTemplate;

    public void sendCustomerCreationRequest(@Valid CustomerCreationRequest request) {
        log.info(LogList.LOG_KAFKA_SEND_MESSAGE, request);
        Message<CustomerCreationRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, customerTopic)
                .build();
        kafkaTemplate.send(message);
    }
}