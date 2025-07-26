package com.alier.productservice.product.infrastructure.outbox.web;

import com.alier.productservice.product.infrastructure.outbox.OutboxEventRelayer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for monitoring the Outbox Pattern implementation.
 * Provides endpoints for operations teams to monitor event processing status.
 */
@RestController
@RequestMapping("/outbox")
@RequiredArgsConstructor
public class OutboxMonitoringController {

    private final OutboxEventRelayer outboxEventRelayer;

    /**
     * Returns current outbox metrics including pending and failed event counts.
     * This endpoint can be used for monitoring and alerting.
     *
     * @return OutboxMetrics containing current status
     */
    @GetMapping("/metrics")
    public ResponseEntity<OutboxEventRelayer.OutboxMetrics> getMetrics() {
        try {
            OutboxEventRelayer.OutboxMetrics metrics = outboxEventRelayer.getMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint that returns OK if outbox system is functioning.
     * Can be integrated with Spring Boot Actuator health checks.
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<OutboxHealthStatus> getHealth() {
        try {
            OutboxEventRelayer.OutboxMetrics metrics = outboxEventRelayer.getMetrics();

            // Consider unhealthy if there are too many failed events
            boolean isHealthy = metrics.failedEvents() < 100; // Configurable threshold

            OutboxHealthStatus status = new OutboxHealthStatus(
                    isHealthy ? "UP" : "DOWN",
                    metrics.pendingEvents(),
                    metrics.failedEvents(),
                    isHealthy ? "Outbox system is functioning normally" : "Too many failed events"
            );

            return isHealthy ?
                    ResponseEntity.ok(status) :
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);

        } catch (Exception e) {
            OutboxHealthStatus status = new OutboxHealthStatus(
                    "DOWN", 0L, 0L, "Error checking outbox health: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }
    }

    /**
     * Health status record for the outbox system.
     */
    public record OutboxHealthStatus(
            String status,
            long pendingEvents,
            long failedEvents,
            String message
    ) {
    }
} 