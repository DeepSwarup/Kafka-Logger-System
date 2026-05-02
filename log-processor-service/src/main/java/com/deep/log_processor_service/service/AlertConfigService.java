package com.deep.log_processor_service.service;

import com.deep.log_processor_service.model.AlertConfig;
import com.deep.log_processor_service.repository.AlertConfigRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AlertConfigService {

    private final AlertConfigRepository repository;


    public AlertConfigService(AlertConfigRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "alertConfigs", key = "#serviceName")
    public AlertConfig getConfig(String serviceName){
        return repository.findByServiceName(serviceName).orElseGet(()-> defaultConfig());
    }

    private AlertConfig defaultConfig() {
        AlertConfig config = new AlertConfig();
        config.setErrorThreshold(20);
        config.setTimeWindowSeconds(60);
        return config;
    }
}
