package com.alier.productservice.product.infrastructure.outbox.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class for the Outbox Pattern implementation.
 * Provides necessary beans and enables scheduling for the OutboxEventRelayer.
 */
@Configuration
@EnableScheduling
public class OutboxConfiguration {

    /**
     * Configures ObjectMapper for JSON serialization/deserialization of domain events.
     * Includes JSR310 module for LocalDateTime support and other time-based types.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register JSR310 module for Java time types (LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());

        // Disable writing dates as timestamps for better readability
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configure additional settings as needed
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return mapper;
    }
} 