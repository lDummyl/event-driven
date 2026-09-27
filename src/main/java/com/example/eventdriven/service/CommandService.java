package com.example.eventdriven.service;

import java.util.UUID;

import com.example.eventdriven.config.RatingProperties;
import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.domain.DonationReceived;
import com.example.eventdriven.domain.EmailChangeRequested;
import com.example.eventdriven.domain.EmailChangeResult;
import com.example.eventdriven.domain.EmailPolicy;
import com.example.eventdriven.domain.UsernameChangeRejected;
import com.example.eventdriven.domain.UsernameChanged;
import org.springframework.stereotype.Service;

/**
 * Online entry point for user actions. It only turns commands into events; the {@link EventRouter}
 * (the proxy) decides how each event is processed.
 */
@Service
public class CommandService {

    private final EventRouter router;
    private final RatingProperties rating;
    private final EmailPolicy emailPolicy;

    public CommandService(EventRouter router, RatingProperties rating, EmailPolicy emailPolicy) {
        this.router = router;
        this.rating = rating;
        this.emailPolicy = emailPolicy;
    }

    public void donate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation amount must be positive");
        }
        long points = Math.floorDiv(amount, 100L) * rating.getPointsPer100();
        router.dispatch(DonationReceived.of(amount, points));
    }

    /**
     * Validation lives in the handler, so the online path rejects a digits-only name while the
     * historical replay path accepts it. The rejection is still recorded in the log.
     */
    public void changeUsername(String name) {
        try {
            router.dispatch(UsernameChanged.of(name));
        } catch (IllegalArgumentException e) {
            router.appendRaw(UsernameChangeRejected.of(name, e.getMessage()));
            throw e;
        }
    }

    /**
     * Email change is a request/response pair sharing one {@code correlationId}. Online, the current
     * policy decides; the decision is stored and later found by correlation during replay.
     */
    public void changeEmail(String email) {
        UUID correlationId = UUID.randomUUID();
        router.appendRaw(EmailChangeRequested.of(correlationId, email));
        try {
            emailPolicy.validate(email);
            router.dispatch(EmailChangeResult.accepted(correlationId, email));
        } catch (IllegalArgumentException e) {
            router.appendRaw(EmailChangeResult.rejected(correlationId, email, e.getMessage()));
            throw e;
        }
    }

    /** Writes a username change straight into the log, as if old rules had accepted it. */
    public void seedLegacyUsername(String name) {
        router.appendRaw(UsernameChanged.of(name));
    }

    /** Writes an accepted email change request/response pair straight into the log (old rules). */
    public void seedLegacyEmail(String email) {
        UUID correlationId = UUID.randomUUID();
        router.appendRaw(EmailChangeRequested.of(correlationId, email));
        router.appendRaw(EmailChangeResult.accepted(correlationId, email));
    }
}
