package com.example.eventdriven.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;

/**
 * Request half of an email change. {@code FROZEN}. During a rebuild this event looks up its recorded
 * {@link EmailChangeResult} by {@code correlationId} and trusts that answer instead of re-validating
 * the email against the current rules.
 */
public record EmailChangeRequested(
        UUID id,
        UUID correlationId,
        Instant occurredAt,
        String newEmail) implements DomainEvent {

    public static EmailChangeRequested of(UUID correlationId, String newEmail) {
        return new EmailChangeRequested(UUID.randomUUID(), correlationId, Instant.now(), newEmail);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
