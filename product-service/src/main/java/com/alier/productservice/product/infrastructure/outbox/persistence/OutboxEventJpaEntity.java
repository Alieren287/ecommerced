package com.alier.productservice.product.infrastructure.outbox.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing an outbox event for reliable event publishing.
 * This entity ensures domain events are persisted atomically with business data
 * and later published to Kafka by the OutboxEventRelayer.
 */
@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, createdAt"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregateType, aggregateId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "delivery_attempts", nullable = false)
    private Integer deliveryAttempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    /**
     * Creates a new outbox event in PENDING status.
     */
    public static OutboxEventJpaEntity create(UUID aggregateId, String aggregateType,
                                              String eventType, String payload) {
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setAggregateId(aggregateId);
        entity.setAggregateType(aggregateType);
        entity.setEventType(eventType);
        entity.setPayload(payload);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus(OutboxEventStatus.PENDING);
        entity.setDeliveryAttempts(0);
        return entity;
    }

    /**
     * Marks the event as published successfully.
     */
    public void markAsPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.processedAt = LocalDateTime.now();
        this.lastError = null;
    }

    /**
     * Marks the event as failed with error details.
     */
    public void markAsFailed(String errorMessage) {
        this.status = OutboxEventStatus.FAILED;
        this.deliveryAttempts++;
        this.lastError = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Resets event to pending for retry.
     */
    public void resetToPending() {
        this.status = OutboxEventStatus.PENDING;
        this.processedAt = null;
        this.lastError = null;
    }

    /**
     * Checks if the event can be retried based on delivery attempts.
     */
    public boolean canRetry(int maxRetries) {
        return this.deliveryAttempts < maxRetries;
    }

    public enum OutboxEventStatus {
        PENDING,
        PUBLISHED,
        FAILED
    }
} 