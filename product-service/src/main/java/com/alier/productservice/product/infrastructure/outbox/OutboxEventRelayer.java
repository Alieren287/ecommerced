package com.alier.productservice.product.infrastructure.outbox;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.infrastructure.outbox.persistence.OutboxEventJpaEntity;
import com.alier.productservice.product.infrastructure.outbox.persistence.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for polling the outbox table and publishing events to Kafka.
 * Runs as a scheduled job to ensure reliable event delivery.
 * Can be disabled by setting outbox.relayer.enabled=false
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "outbox.relayer.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxEventRelayer {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${outbox.relayer.batch-size:50}")
    private int batchSize;

    @Value("${outbox.relayer.max-retries:3}")
    private int maxRetries;

    @Value("${outbox.relayer.retry-delay-minutes:5}")
    private int retryDelayMinutes;

    @Value("${outbox.relayer.kafka-topic:product-events}")
    private String kafkaTopic;

    /**
     * Scheduled method that polls for pending events and publishes them to Kafka.
     * Runs every 10 seconds by default.
     */
    @Scheduled(fixedDelayString = "${outbox.relayer.poll-interval-ms:10000}")
    @Transactional
    public void processOutboxEvents() {
        try {
            log.debug("Starting outbox event processing");

            // Process pending events
            Pageable pageable = PageRequest.of(0, batchSize);
            List<OutboxEventJpaEntity> pendingEvents = outboxEventRepository
                    .findPendingEventsForProcessing(pageable);

            if (!pendingEvents.isEmpty()) {
                log.info("Processing {} pending outbox events", pendingEvents.size());
                processEvents(pendingEvents);
            }

            // Process failed events that are eligible for retry
            LocalDateTime retryAfter = LocalDateTime.now().minusMinutes(retryDelayMinutes);
            List<OutboxEventJpaEntity> failedEvents = outboxEventRepository
                    .findFailedEventsForRetry(maxRetries, retryAfter, pageable);

            if (!failedEvents.isEmpty()) {
                log.info("Retrying {} failed outbox events", failedEvents.size());
                // Reset failed events to pending for retry
                failedEvents.forEach(OutboxEventJpaEntity::resetToPending);
                outboxEventRepository.saveAll(failedEvents);
                processEvents(failedEvents);
            }

            log.debug("Completed outbox event processing");

        } catch (Exception e) {
            log.error("Error during outbox event processing", e);
        }
    }

    /**
     * Processes a list of outbox events by publishing them to Kafka.
     */
    private void processEvents(List<OutboxEventJpaEntity> events) {
        for (OutboxEventJpaEntity event : events) {
            try {
                publishEvent(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: id={}, eventType={}, error={}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
                event.markAsFailed("Failed to publish: " + e.getMessage());
                outboxEventRepository.save(event);
            }
        }
    }

    /**
     * Publishes a single outbox event to Kafka.
     */
    private void publishEvent(OutboxEventJpaEntity outboxEvent) {
        try {
            // Deserialize the event payload
            DomainEvent domainEvent = deserializeEvent(outboxEvent);

            // Create Kafka message key using aggregate ID for ordering
            String messageKey = outboxEvent.getAggregateId().toString();

            // Publish to Kafka asynchronously
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                    .send(kafkaTopic, messageKey, domainEvent);

            // Handle success/failure
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to publish event to Kafka: eventId={}, error={}",
                            outboxEvent.getId(), throwable.getMessage(), throwable);
                    outboxEvent.markAsFailed("Kafka publish failed: " + throwable.getMessage());
                } else {
                    log.debug("Successfully published event to Kafka: eventId={}, topic={}, partition={}, offset={}",
                            outboxEvent.getId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    outboxEvent.markAsPublished();
                }
                outboxEventRepository.save(outboxEvent);
            });

        } catch (Exception e) {
            log.error("Error publishing outbox event: id={}, error={}",
                    outboxEvent.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deserializes the JSON payload back to a domain event object.
     */
    private DomainEvent deserializeEvent(OutboxEventJpaEntity outboxEvent) {
        try {
            // Get the event class
            Class<?> eventClass = Class.forName(outboxEvent.getEventType());

            // Deserialize the JSON payload
            Object eventObject = objectMapper.readValue(outboxEvent.getPayload(), eventClass);

            if (!(eventObject instanceof DomainEvent)) {
                throw new IllegalArgumentException("Deserialized object is not a DomainEvent: " + eventClass.getName());
            }

            return (DomainEvent) eventObject;

        } catch (Exception e) {
            throw new OutboxEventDeserializationException(
                    "Failed to deserialize outbox event: " + outboxEvent.getId(), e);
        }
    }

    /**
     * Cleanup method to remove old published events.
     * Runs daily at 2 AM by default.
     */
    @Scheduled(cron = "${outbox.relayer.cleanup-cron:0 0 2 * * ?}")
    @Transactional
    public void cleanupPublishedEvents() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // Keep events for 7 days
            int deletedCount = outboxEventRepository.deletePublishedEventsBefore(cutoffDate);

            if (deletedCount > 0) {
                log.info("Cleaned up {} published outbox events older than {}", deletedCount, cutoffDate);
            }

        } catch (Exception e) {
            log.error("Error during outbox event cleanup", e);
        }
    }

    /**
     * Provides monitoring information about outbox events.
     */
    public OutboxMetrics getMetrics() {
        long pendingCount = outboxEventRepository.countPendingEvents();
        long failedCount = outboxEventRepository.countFailedEvents();

        return new OutboxMetrics(pendingCount, failedCount);
    }

    /**
     * Custom exception for event deserialization failures.
     */
    public static class OutboxEventDeserializationException extends RuntimeException {
        public OutboxEventDeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Simple metrics class for monitoring outbox events.
     */
    public record OutboxMetrics(long pendingEvents, long failedEvents) {
    }
} 