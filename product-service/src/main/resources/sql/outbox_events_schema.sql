-- Outbox Events Table for the Outbox Pattern
-- This table stores domain events that need to be published to Kafka
-- Ensures atomic persistence of business data and events

CREATE TABLE IF NOT EXISTS outbox_events
(
    id
    UUID
    PRIMARY
    KEY,
    aggregate_type
    VARCHAR
(
    100
) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR
(
    255
) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR
(
    20
) NOT NULL CHECK
(
    status
    IN
(
    'PENDING',
    'PUBLISHED',
    'FAILED'
)),
    processed_at TIMESTAMP,
    delivery_attempts INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR
(
    1000
)
    );

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_outbox_status_created
    ON outbox_events (status, created_at);

CREATE INDEX IF NOT EXISTS idx_outbox_aggregate
    ON outbox_events (aggregate_type, aggregate_id);

-- Index for failed event retries
CREATE INDEX IF NOT EXISTS idx_outbox_failed_retry
    ON outbox_events (status, delivery_attempts, processed_at)
    WHERE status = 'FAILED';

-- Comments for documentation
COMMENT
ON TABLE outbox_events IS 'Stores domain events for reliable publishing via the Outbox Pattern';
COMMENT
ON COLUMN outbox_events.id IS 'Unique identifier for the outbox event';
COMMENT
ON COLUMN outbox_events.aggregate_type IS 'Type of aggregate that generated the event (e.g., Product, ProductVariant)';
COMMENT
ON COLUMN outbox_events.aggregate_id IS 'ID of the aggregate instance that generated the event';
COMMENT
ON COLUMN outbox_events.event_type IS 'Full class name of the domain event';
COMMENT
ON COLUMN outbox_events.payload IS 'JSON serialized domain event data';
COMMENT
ON COLUMN outbox_events.created_at IS 'Timestamp when the event was created';
COMMENT
ON COLUMN outbox_events.status IS 'Current status of the event (PENDING, PUBLISHED, FAILED)';
COMMENT
ON COLUMN outbox_events.processed_at IS 'Timestamp when the event was last processed';
COMMENT
ON COLUMN outbox_events.delivery_attempts IS 'Number of delivery attempts made';
COMMENT
ON COLUMN outbox_events.last_error IS 'Last error message if delivery failed';