package com.example.eventdriven.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;

/**
 * Username was accepted. {@code FROZEN}: if the current rules would now reject this name (e.g. digits
 * only) a rebuild still applies it, because it is a historical fact.
 */
public record UsernameChanged(
        UUID id,
        UUID correlationId,
        Instant occurredAt,
        String newName) implements DomainEvent {

    public static UsernameChanged of(String newName) {
        return new UsernameChanged(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), newName);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
