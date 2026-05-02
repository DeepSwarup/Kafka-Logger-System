package com.deep.log_producer_service.controller;

import com.deep.log_producer_service.model.LogEvent;
import com.deep.log_producer_service.service.KafkaProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final KafkaProducerService kafkaProducerService;

    public LogController(KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public ResponseEntity<String> sendLog(@RequestBody String logEvent){
        kafkaProducerService.sendLog(logEvent);
        return new ResponseEntity<>("log sent", HttpStatus.OK);
    }
}
