package com.example.eventdriven.core;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A fact that happened and is stored forever in the log.
 *
 * <p>Implementations are records carrying an {@link EventMetadata} envelope plus their own payload.
 * Everything the framework needs is expressed through these accessors, so adding a new event is just
 * a record + one {@link EventHandler} bean.</p>
 */
public interface DomainEvent {

    EventMetadata metadata();

    /**
     * The single divide that drives processing: {@code RECALCULABLE} is recomputed by current logic
     * on rebuild, {@code FROZEN} is applied as a historical fact.
     */
    EventKind kind();

    @JsonIgnore
    default UUID id() {
        return metadata().id();
    }

    /**
     * Links an event to the request it answers. Used during a rebuild to locate the recorded
     * response (e.g. the acknowledgment of an email change) without re-running validation.
     */
    @JsonIgnore
    default UUID correlationId() {
        return metadata().correlationId();
    }

    /** The aggregate this fact belongs to (e.g. {@code user:1}). */
    @JsonIgnore
    default String aggregateId() {
        return metadata().aggregateId();
    }

    @JsonIgnore
    default Instant occurredAt() {
        return metadata().occurredAt();
    }
}
