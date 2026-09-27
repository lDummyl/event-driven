package com.example.eventdriven.log;

import java.util.List;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventHandlerRegistry;
import com.example.eventdriven.core.EventMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Turns events into NDJSON lines and back. The envelope is the single carrier of metadata; the
 * payload stores only event-specific fields. The event type name is resolved through the handler
 * registry, so adding an event + handler is enough for it to be persistable.
 */
@Component
public class EventCodec {

    private final ObjectMapper mapper;
    private final EventHandlerRegistry registry;

    public EventCodec(ObjectMapper mapper, EventHandlerRegistry registry) {
        this.mapper = mapper;
        this.registry = registry;
    }

    public String encode(DomainEvent event) {
        try {
            ObjectNode payload = (ObjectNode) mapper.valueToTree(event);
            // Envelope fields live only in the log entry, never in the payload.
            payload.remove(List.of("kind", "metadata", "id", "correlationId", "aggregateId", "occurredAt"));

            EventMetadata meta = event.metadata();
            EventLogEntry entry = new EventLogEntry(
                    meta.id(),
                    meta.correlationId(),
                    meta.causationId(),
                    meta.aggregateId(),
                    meta.schemaVersion(),
                    event.getClass().getName(),
                    meta.occurredAt(),
                    payload);
            return mapper.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot encode event " + event, e);
        }
    }

    public DomainEvent decode(String line) {
        try {
            EventLogEntry entry = mapper.readValue(line, EventLogEntry.class);
            Class<? extends DomainEvent> type = registry.classFor(entry.type());

            EventMetadata meta = new EventMetadata(
                    entry.id(),
                    entry.correlationId(),
                    entry.causationId(),
                    entry.aggregateId(),
                    entry.occurredAt(),
                    entry.schemaVersion());

            ObjectNode full = ((ObjectNode) entry.payload()).deepCopy();
            full.set("metadata", mapper.valueToTree(meta));
            return mapper.treeToValue(full, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot decode log line: " + line, e);
        }
    }
}
