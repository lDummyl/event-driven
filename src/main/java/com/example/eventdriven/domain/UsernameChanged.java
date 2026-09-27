package com.example.eventdriven.domain;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;
import com.example.eventdriven.core.EventMetadata;

/**
 * Username was accepted. {@code FROZEN}: if the current rules would now reject this name (e.g. digits
 * only) a rebuild still applies it, because it is a historical fact.
 */
public record UsernameChanged(
        EventMetadata metadata,
        String newName) implements DomainEvent {

    public static UsernameChanged of(String aggregateId, String newName) {
        return new UsernameChanged(EventMetadata.of(aggregateId), newName);
    }

    @Override
    public EventKind kind() {
        return EventKind.FROZEN;
    }
}
