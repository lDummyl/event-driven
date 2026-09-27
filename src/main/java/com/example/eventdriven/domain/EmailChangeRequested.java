package com.example.eventdriven.domain;

import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;
import com.example.eventdriven.core.EventMetadata;

/**
 * Request half of an email change. {@code FROZEN}. During a rebuild this event looks up its recorded
 * {@link EmailChangeResult} by {@code correlationId} and trusts that answer instead of re-validating
 * the email against the current rules.
 */
public record EmailChangeRequested(
        EventMetadata metadata,
        String newEmail) implements DomainEvent {

    public static EmailChangeRequested of(String aggregateId, UUID correlationId, String newEmail) {
        return new EmailChangeRequested(EventMetadata.of(aggregateId, correlationId), newEmail);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
