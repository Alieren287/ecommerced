package com.alier.productservice.product.infrastructure.outbox.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for OutboxEventJpaEntity.
 * Provides methods for polling pending events and managing event lifecycle.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    /**
     * Finds pending events for processing with pessimistic locking.
     * Uses FOR UPDATE SKIP LOCKED to handle concurrent processing safely.
     *
     * @param pageable Pagination information (contains limit)
     * @return List of pending events, locked for processing
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM outbox_events e WHERE e.status = 'PENDING' " +
            "ORDER BY e.created_at ASC FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    List<OutboxEventJpaEntity> findPendingEventsForProcessing(Pageable pageable);

    /**
     * Finds failed events that can be retried.
     *
     * @param maxRetries Maximum number of retry attempts
     * @param retryAfter Only retry events that failed before this time
     * @param pageable   Pagination information (contains limit)
     * @return List of failed events eligible for retry
     */
    @Query("SELECT e FROM OutboxEventJpaEntity e WHERE e.status = 'FAILED' " +
            "AND e.deliveryAttempts < :maxRetries " +
            "AND e.processedAt < :retryAfter " +
            "ORDER BY e.createdAt ASC")
    List<OutboxEventJpaEntity> findFailedEventsForRetry(@Param("maxRetries") int maxRetries,
                                                        @Param("retryAfter") LocalDateTime retryAfter,
                                                        Pageable pageable);

    /**
     * Counts pending events.
     */
    @Query("SELECT COUNT(e) FROM OutboxEventJpaEntity e WHERE e.status = 'PENDING'")
    long countPendingEvents();

    /**
     * Counts failed events.
     */
    @Query("SELECT COUNT(e) FROM OutboxEventJpaEntity e WHERE e.status = 'FAILED'")
    long countFailedEvents();

    /**
     * Deletes successfully published events older than the specified date.
     * This is useful for cleanup to prevent the outbox table from growing indefinitely.
     *
     * @param beforeDate Delete published events created before this date
     * @return Number of deleted events
     */
    @Modifying
    @Query("DELETE FROM OutboxEventJpaEntity e WHERE e.status = 'PUBLISHED' AND e.createdAt < :beforeDate")
    int deletePublishedEventsBefore(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Finds events by aggregate ID for debugging/monitoring purposes.
     */
    List<OutboxEventJpaEntity> findByAggregateIdOrderByCreatedAtDesc(UUID aggregateId);

    /**
     * Finds events by aggregate type and status.
     */
    List<OutboxEventJpaEntity> findByAggregateTypeAndStatus(String aggregateType,
                                                            OutboxEventJpaEntity.OutboxEventStatus status);
} 