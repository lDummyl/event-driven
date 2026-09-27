package com.example.eventdriven.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;

/** Response half of an email change, linked to its request by {@code correlationId}. {@code FROZEN}. */
public record EmailChangeResult(
        UUID id,
        UUID correlationId,
        Instant occurredAt,
        String email,
        boolean accepted,
        String reason) implements DomainEvent {

    public static EmailChangeResult accepted(UUID correlationId, String email) {
        return new EmailChangeResult(UUID.randomUUID(), correlationId, Instant.now(), email, true, null);
    }

    public static EmailChangeResult rejected(UUID correlationId, String email, String reason) {
        return new EmailChangeResult(UUID.randomUUID(), correlationId, Instant.now(), email, false, reason);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
