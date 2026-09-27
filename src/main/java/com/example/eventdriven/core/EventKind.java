package com.example.eventdriven.core;

/**
 * The single divide that drives the whole design.
 *
 * <ul>
 *   <li>{@link #RECALCULABLE} - the effect of the event is not trusted; during a rebuild it is
 *       recomputed by the <em>current</em> business logic (e.g. donation points).</li>
 *   <li>{@link #FROZEN} - the event is a historical fact. During a rebuild it is applied as-is and
 *       is never re-validated against the new rules (e.g. a username that would now be rejected).</li>
 * </ul>
 */
public enum EventKind {
    RECALCULABLE,
    FROZEN
}
