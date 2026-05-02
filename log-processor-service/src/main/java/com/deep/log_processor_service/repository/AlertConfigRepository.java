package com.deep.log_processor_service.repository;

import com.deep.log_processor_service.model.AlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertConfigRepository extends JpaRepository<AlertConfig, Long> {
    Optional<AlertConfig> findByServiceName(String serviceName);
}
