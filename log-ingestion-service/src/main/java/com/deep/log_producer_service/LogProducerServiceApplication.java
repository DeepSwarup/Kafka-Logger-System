package com.deep.log_producer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
public class LogProducerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogProducerServiceApplication.class, args);
	}

}
