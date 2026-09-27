package com.example.eventdriven.domain;

import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventKind;
import com.example.eventdriven.core.EventMetadata;

/**
 * A donation. {@code RECALCULABLE}: the stored {@code pointsAwarded} is informational only - during a
 * rebuild the points are recomputed from {@code amount} using the current rating rule.
 */
public record DonationReceived(
        EventMetadata metadata,
        long amount,
        long pointsAwarded) implements DomainEvent {

    public static DonationReceived of(String aggregateId, long amount, long pointsAwarded) {
        return new DonationReceived(EventMetadata.of(aggregateId), amount, pointsAwarded);
    }

    @Override
    public EventKind kind() {
        return EventKind.RECALCULABLE;
    }
}
