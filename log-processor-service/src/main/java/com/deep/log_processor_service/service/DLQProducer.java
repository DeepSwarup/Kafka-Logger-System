package com.deep.log_processor_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DLQProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String DLQ_TOPIC = "logs-dlq";

    public DLQProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToDLQ(String message) {
        kafkaTemplate.send(DLQ_TOPIC, message);
    }

}
