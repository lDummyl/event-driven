package com.example.eventdriven.domain;

import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;
import com.example.eventdriven.core.EventMetadata;

/** Response half of an email change, linked to its request by {@code correlationId}. {@code FROZEN}. */
public record EmailChangeResult(
        EventMetadata metadata,
        String email,
        boolean accepted,
        String reason) implements DomainEvent {

    public static EmailChangeResult accepted(String aggregateId, UUID correlationId, UUID causationId,
                                             String email) {
        return new EmailChangeResult(EventMetadata.of(aggregateId, correlationId, causationId),
                email, true, null);
    }

    public static EmailChangeResult rejected(String aggregateId, UUID correlationId, UUID causationId,
                                             String email, String reason) {
        return new EmailChangeResult(EventMetadata.of(aggregateId, correlationId, causationId),
                email, false, reason);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
