package com.deep.log_processor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableCaching
public class LogProcessorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogProcessorServiceApplication.class, args);
	}

}
