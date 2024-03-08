package com.nikonenko.driverservice.kafka.producer;

import com.nikonenko.driverservice.dto.ChangeRideStatusRequest;
import com.nikonenko.driverservice.utils.LogList;
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
public class RideStatusRequestProducer {
    @Value("${spring.kafka.producer.status-producer-topic.name}")
    private String driverTopic;
    private final KafkaTemplate<String, ChangeRideStatusRequest> kafkaTemplate;

    public void sendChangeRideStatusRequest(ChangeRideStatusRequest request) {
        log.info(LogList.LOG_KAFKA_SEND_MESSAGE, request);
        Message<ChangeRideStatusRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, driverTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
