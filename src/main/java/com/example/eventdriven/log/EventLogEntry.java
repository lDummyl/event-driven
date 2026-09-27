package com.example.eventdriven.log;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * One line in the NDJSON log: the identity/envelope plus the concrete payload. Storing the type name
 * explicitly keeps the log readable and decoupled from Jackson polymorphism.
 */
public record EventLogEntry(
        UUID id,
        UUID correlationId,
        String type,
        Instant occurredAt,
        JsonNode payload) {
}
