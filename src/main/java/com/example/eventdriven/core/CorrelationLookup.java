package com.example.eventdriven.core;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-only view over history that handlers may use while replaying {@link EventKind#FROZEN}
 * events to find the recorded answer to a correlated request.
 */
public interface CorrelationLookup {

    <T extends DomainEvent> Optional<T> findResponse(UUID correlationId, Class<T> type);
}
