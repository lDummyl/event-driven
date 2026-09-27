package com.example.eventdriven.core;

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
 *
 * <p>While rebuilding the router is in {@link Mode#REBUILD}; side effects (see {@link EffectPort})
 * must observe {@link #currentMode()} and stay inert.</p>
 */
@Component
public class EventRouter {

    private final EventHandlerRegistry registry;
    private final EventStore eventStore;
    private final UserStateService state;

    private Mode mode = Mode.ONLINE;

    public EventRouter(EventHandlerRegistry registry, EventStore eventStore, UserStateService state) {
        this.registry = registry;
        this.eventStore = eventStore;
        this.state = state;
    }

    public synchronized Mode currentMode() {
        return mode;
    }

    /** Online path: current rules + validation, then store the fact forever. */
    public synchronized void dispatch(DomainEvent event) {
        state.applyIfNew(event.id(), () -> registry.handlerFor(event).apply(state.live(), event));
        eventStore.append(event);
        state.persist();
    }

    /** Historical path: append to the log as-is, without any current validation. */
    public synchronized void appendRaw(DomainEvent event) {
        eventStore.append(event);
    }

    /** Recreate the state from scratch by replaying the eternal log. */
    public synchronized void rebuild() {
        mode = Mode.REBUILD;
        try {
            state.reset();
            for (DomainEvent event : eventStore.all()) {
                EventHandler<DomainEvent> handler = registry.handlerFor(event);
                state.applyIfNew(event.id(), () -> {
                    if (event.kind() == EventKind.RECALCULABLE) {
                        handler.apply(state.live(), event);
                    } else {
                        handler.replay(state.live(), event, eventStore);
                    }
                });
            }
            state.persist();
        } finally {
            mode = Mode.ONLINE;
        }
    }
}
