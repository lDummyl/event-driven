package com.example.eventdriven.log;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventHandlerRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Turns events into NDJSON lines and back. The event type name is resolved through the handler
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
            payload.remove("kind"); // derived from the class, not part of the stored shape
            EventLogEntry entry = new EventLogEntry(
                    event.id(),
                    event.correlationId(),
                    event.getClass().getName(),
                    event.occurredAt(),
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
            return mapper.treeToValue(entry.payload(), type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot decode log line: " + line, e);
        }
    }
}
