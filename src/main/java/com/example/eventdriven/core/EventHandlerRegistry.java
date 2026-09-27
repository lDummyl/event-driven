package com.example.eventdriven.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Discovers every {@link EventHandler} bean and indexes it by event class and by class name
 * (the name is what the on-disk log stores). No switch statements anywhere.
 */
@Component
public class EventHandlerRegistry {

    private final Map<Class<?>, EventHandler<?>> byClass = new HashMap<>();
    private final Map<String, Class<? extends DomainEvent>> byName = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EventHandlerRegistry(List<EventHandler<?>> handlers) {
        for (EventHandler<?> handler : handlers) {
            Class<?> type = handler.eventType();
            if (byClass.put(type, handler) != null) {
                throw new IllegalStateException("Duplicate handler for " + type.getName());
            }
            byName.put(type.getName(), (Class<? extends DomainEvent>) type);
        }
    }

    @SuppressWarnings("unchecked")
    public EventHandler<DomainEvent> handlerFor(DomainEvent event) {
        EventHandler<?> handler = byClass.get(event.getClass());
        if (handler == null) {
            throw new IllegalStateException("No handler registered for " + event.getClass().getName());
        }
        return (EventHandler<DomainEvent>) handler;
    }

    public Class<? extends DomainEvent> classFor(String name) {
        Class<? extends DomainEvent> type = byName.get(name);
        if (type == null) {
            throw new IllegalStateException("Unknown event type in log: " + name);
        }
        return type;
    }

    public Set<Class<?>> knownTypes() {
        return Set.copyOf(byClass.keySet());
    }
}
