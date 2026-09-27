package com.example.eventdriven.core;

import com.example.eventdriven.state.UserState;

/**
 * Applies a single event type to the state.
 *
 * <p>Two entry points exist on purpose:</p>
 * <ul>
 *   <li>{@link #apply} - the live rules. Called online and for {@link EventKind#RECALCULABLE} during
 *       a rebuild, where validation under the current rules is desired.</li>
 *   <li>{@link #replay} - the historical path for {@link EventKind#FROZEN} events. It must apply the
 *       fact without re-validating it. The default delegates to {@link #apply}, override it when the
 *       fact could otherwise be rejected by the current rules.</li>
 * </ul>
 */
public interface EventHandler<E extends DomainEvent> {

    Class<E> eventType();

    void apply(UserState state, E event);

    default void replay(UserState state, E event, CorrelationLookup log) {
        apply(state, event);
    }
}
