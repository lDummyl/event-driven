package com.example.eventdriven.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;

/** A username change that the current rules rejected. Stored so the log tells the whole story. */
public record UsernameChangeRejected(
        UUID id,
        UUID correlationId,
        Instant occurredAt,
        String attemptedName,
        String reason) implements DomainEvent {

    public static UsernameChangeRejected of(String attemptedName, String reason) {
        return new UsernameChangeRejected(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), attemptedName, reason);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
