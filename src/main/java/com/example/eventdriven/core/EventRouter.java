package com.example.eventdriven.core;

import java.util.List;

import com.example.eventdriven.log.EventLog;
import com.example.eventdriven.state.UserStateService;
import org.springframework.stereotype.Component;

/**
 * The proxy. It is the single entry point for events and it decides what "processing" means:
 *
 * <ul>
 *   <li>{@link #dispatch} - online. Runs the current logic (which validates and may reject), then
 *       appends to the eternal log.</li>
 *   <li>{@link #appendRaw} - writes a historical fact straight into the log, bypassing all current
 *       logic. This is how the past (old rules) is recorded.</li>
 *   <li>{@link #rebuild} - recreates the state from the log. It routes each event by its
 *       {@link EventKind}: {@code RECALCULABLE} goes through the main service under current logic,
 *       {@code FROZEN} goes through the historical path.</li>
 * </ul>
 */
@Component
public class EventRouter {

    private final EventHandlerRegistry registry;
    private final EventLog eventLog;
    private final UserStateService state;

    public EventRouter(EventHandlerRegistry registry, EventLog eventLog, UserStateService state) {
        this.registry = registry;
        this.eventLog = eventLog;
        this.state = state;
    }

    /** Online path: current rules + validation, then store the fact forever. */
    public synchronized void dispatch(DomainEvent event) {
        registry.handlerFor(event).apply(state.live(), event);
        eventLog.append(event);
        state.persist();
    }

    /** Historical path: append to the log as-is, without any current validation. */
    public synchronized void appendRaw(DomainEvent event) {
        eventLog.append(event);
    }

    /** Recreate the state from scratch by replaying the eternal log. */
    public synchronized void rebuild() {
        state.reset();
        List<DomainEvent> history = eventLog.all();
        for (DomainEvent event : history) {
            EventHandler<DomainEvent> handler = registry.handlerFor(event);
            if (event.kind() == EventKind.RECALCULABLE) {
                handler.apply(state.live(), event);
            } else {
                handler.replay(state.live(), event, eventLog);
            }
        }
        state.persist();
    }
}
