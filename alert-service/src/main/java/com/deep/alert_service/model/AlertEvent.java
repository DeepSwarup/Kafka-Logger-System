package com.deep.alert_service.model;

import lombok.Data;

@Data
public class AlertEvent {

    private String serviceName;
    private String alertType;
    private int count;
    private String timeWindow;
    private long timestamp;

}
