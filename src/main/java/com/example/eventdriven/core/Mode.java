package com.example.eventdriven.core;

/**
 * What the router is currently doing.
 *
 * <ul>
 *   <li>{@link #ONLINE} - live processing; side effects may fire.</li>
 *   <li>{@link #REBUILD} - recreating state from the log; side effects must stay inert.</li>
 * </ul>
 *
 * This is the seam that keeps future side effects (emails, payments, webhooks) from firing during a
 * rebuild. See {@link EffectPort}.
 */
public enum Mode {
    ONLINE,
    REBUILD
}
