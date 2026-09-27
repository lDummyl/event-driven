package com.example.eventdriven.core;

/**
 * Marker for side-effect adapters (email, payments, webhooks, ...).
 *
 * <p>Every implementation MUST execute only while the router is in {@link Mode#ONLINE} and stay
 * inert during a {@link Mode#REBUILD}. This interface is intentionally empty for now: it reserves
 * the seam without committing to an outbox implementation yet.</p>
 */
public interface EffectPort {
}
