package com.example.eventdriven.log;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * One line in the NDJSON log: the envelope (identity, correlation, causality, aggregate, schema
 * version, type) plus the concrete payload. The envelope is the only carrier of metadata; the
 * payload holds just the event-specific fields.
 */
public record EventLogEntry(
        UUID id,
        UUID correlationId,
        UUID causationId,
        String aggregateId,
        int schemaVersion,
        String type,
        Instant occurredAt,
        JsonNode payload) {
}
