package com.example.eventdriven.core;

import java.util.List;

/**
 * Port over the eternal fact log. Handlers and services depend on this abstraction only, so the
 * storage behind it (NDJSON file today, database or broker tomorrow) can change without touching
 * them.
 */
public interface EventStore extends CorrelationLookup {

    void append(DomainEvent event);

    void clear();

    List<DomainEvent> all();

    List<DomainEvent> byAggregate(String aggregateId);

    int size();
}
