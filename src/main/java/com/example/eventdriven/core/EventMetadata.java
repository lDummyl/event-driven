package com.example.eventdriven.core;

import java.time.Instant;
import java.util.UUID;

/**
 * The envelope every fact carries: identity, correlation, causality, aggregate and schema version.
 *
 * <p>Keeping these here (instead of on each event record) means future envelope fields do not force a
 * change to every event type - the seam for schema evolution and tracing is already in place.</p>
 *
 * @param causationId the id of the event that caused this one, or {@code null} when none.
 * @param schemaVersion the schema version of the event payload, starts at 1.
 */
public record EventMetadata(
        UUID id,
        UUID correlationId,
        UUID causationId,
        String aggregateId,
        Instant occurredAt,
        int schemaVersion) {

    public static EventMetadata of(String aggregateId) {
        return new EventMetadata(UUID.randomUUID(), UUID.randomUUID(), null, aggregateId, Instant.now(), 1);
    }

    public static EventMetadata of(String aggregateId, UUID correlationId) {
        return new EventMetadata(UUID.randomUUID(), correlationId, null, aggregateId, Instant.now(), 1);
    }

    public static EventMetadata of(String aggregateId, UUID correlationId, UUID causationId) {
        return new EventMetadata(UUID.randomUUID(), correlationId, causationId, aggregateId, Instant.now(), 1);
    }
}
