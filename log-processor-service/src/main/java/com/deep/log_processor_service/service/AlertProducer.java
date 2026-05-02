package com.deep.log_processor_service.service;

import com.deep.log_processor_service.model.AlertEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOPIC = "alerts-topic";


    public AlertProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAlert(AlertEvent alert){
        try {
            String json = objectMapper.writeValueAsString(alert);
            kafkaTemplate.send(TOPIC, alert.getServiceName(), json);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to send alert");
        }
    }
}
