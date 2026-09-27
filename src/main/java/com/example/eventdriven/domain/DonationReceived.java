package com.example.eventdriven.domain;

import java.time.Instant;
import java.util.UUID;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;

/**
 * A donation. {@code RECALCULABLE}: the stored {@code pointsAwarded} is informational only - during a
 * rebuild the points are recomputed from {@code amount} using the current rating rule.
 */
public record DonationReceived(
        UUID id,
        UUID correlationId,
        Instant occurredAt,
        long amount,
        long pointsAwarded) implements DomainEvent {

    public static DonationReceived of(long amount, long pointsAwarded) {
        return new DonationReceived(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), amount, pointsAwarded);
    }

    @Override
    public EventKind kind() {
        return EventKind.RECALCULABLE;
    }
}
