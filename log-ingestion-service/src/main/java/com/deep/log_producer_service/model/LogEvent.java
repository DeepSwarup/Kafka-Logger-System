package com.deep.log_producer_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LogEvent {

    private String serviceName;
    private String logLevel;
    private String message;
    private long timestamp;
}
