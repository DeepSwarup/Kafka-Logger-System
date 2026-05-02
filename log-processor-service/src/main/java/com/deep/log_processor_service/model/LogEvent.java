package com.deep.log_processor_service.model;

import lombok.Data;

@Data
public class LogEvent {

    private String serviceName;
    private String logLevel;
    private String message;
    private long timestamp;
}
