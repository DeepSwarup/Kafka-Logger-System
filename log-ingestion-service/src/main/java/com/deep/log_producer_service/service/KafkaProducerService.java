package com.deep.log_producer_service.service;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {


    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "logs-topic";


    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLog(String logJson){
        for (int i = 0; i < 20; i++) {
            kafkaTemplate.send(TOPIC, logJson);
        }
//        kafkaTemplate.send(TOPIC, logJson);
    }
}
