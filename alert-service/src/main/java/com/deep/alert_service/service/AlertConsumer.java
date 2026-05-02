package com.deep.alert_service.service;

import com.deep.alert_service.model.AlertEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AlertConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "alerts-topic", groupId = "alert-consumer-group")
    public void consume(String message){
        try {
            AlertEvent alert = objectMapper.readValue(message, AlertEvent.class);
            System.out.println("🚨 ALERT RECEIVED:");
            System.out.println(alert);
        } catch (Exception e) {
            System.out.println("Failed to parse alert: " + message);
        }
    }

}
