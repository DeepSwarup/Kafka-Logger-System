package com.deep.log_processor_service.service;

import com.deep.log_processor_service.model.AlertConfig;
import com.deep.log_processor_service.model.AlertEvent;
import com.deep.log_processor_service.model.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class LogConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, List<Long>> errorLogs = new ConcurrentHashMap<>();

//    private static final int THRESHOLD = 20;
//    private static final long WINDOW_SIZE = 60*1000;

    @Autowired
    private AlertProducer alertProducer;

    @Autowired
    private DLQProducer dlqProducer;

    @Autowired
    private AlertConfigService configService;


    @KafkaListener(topics = "logs-topic", groupId = "log-processor-group")
    public void consume(String message){
        try {
            LogEvent log = objectMapper.readValue(message, LogEvent.class);
            processLog(log);
        }catch (Exception e){
            System.out.println("Failed to parse message");
            System.out.println("Sending to DLQ: " + message);
            dlqProducer.sendToDLQ(message);
        }
    }

    private void processLog(LogEvent log) {
        if(!"ERROR".equalsIgnoreCase(log.getLogLevel())) return;

        String service = log.getServiceName();
        long now = log.getTimestamp();

        errorLogs.putIfAbsent(service, new ArrayList<>());
        List<Long> timestamps = errorLogs.get(service);

        timestamps.add(now);

        AlertConfig config = configService.getConfig(service);

        int threshold = config.getErrorThreshold();
        long windowSize = config.getTimeWindowSeconds() * 1000L;

        timestamps.removeIf(ts->now-ts>windowSize);

        if(timestamps.size() >= threshold){

            AlertEvent alert = new AlertEvent();

            alert.setServiceName(service);
            alert.setAlertType("ERROR_SPIKE");
            alert.setCount(timestamps.size());
            alert.setTimeWindow(config.getTimeWindowSeconds() + "s");
            alert.setTimestamp(System.currentTimeMillis());

            alertProducer.sendAlert(alert);

            System.out.println("🚨 ALERT sent for " + service);
        }
    }
}
