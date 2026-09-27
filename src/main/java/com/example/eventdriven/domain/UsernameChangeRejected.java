package com.example.eventdriven.domain;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;
import com.example.eventdriven.core.EventMetadata;

/** A username change that the current rules rejected. Stored so the log tells the whole story. */
public record UsernameChangeRejected(
        EventMetadata metadata,
        String attemptedName,
        String reason) implements DomainEvent {

    public static UsernameChangeRejected of(String aggregateId, String attemptedName, String reason) {
        return new UsernameChangeRejected(EventMetadata.of(aggregateId), attemptedName, reason);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
