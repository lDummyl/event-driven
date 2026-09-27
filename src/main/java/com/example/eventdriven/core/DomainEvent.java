package com.example.eventdriven.core;

import java.time.Instant;
import java.util.UUID;

/**
 * A fact that happened and is stored forever in the log.
 *
 * <p>Implementations are records. Everything the framework needs is expressed through these four
 * accessors, so adding a new event is just: a record + one {@link EventHandler} bean.</p>
 */
public interface DomainEvent {

    UUID id();

    /**
     * Links an event to the request it answers. Used during a rebuild to locate the recorded
     * response (e.g. the acknowledgment of an email change) without re-running validation.
     */
    UUID correlationId();

    EventKind kind();

    Instant occurredAt();
}
