package com.example.eventdriven.service;

import java.util.UUID;

import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.domain.EmailChangeRequested;
import com.example.eventdriven.domain.EmailChangeResult;
import com.example.eventdriven.domain.EmailPolicy;
import com.example.eventdriven.domain.UsernameChangeRejected;
import com.example.eventdriven.domain.UsernameChanged;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Service;

/**
 * Operations over the user's primary data: username and email. Turns commands into events; the
 * {@link EventRouter} (the proxy) decides how each event is processed.
 */
@Service
public class UserPrimalDataService {

    private final EventRouter router;
    private final EmailPolicy emailPolicy;

    public UserPrimalDataService(EventRouter router, EmailPolicy emailPolicy) {
        this.router = router;
        this.emailPolicy = emailPolicy;
    }

    /**
     * Validation lives in the handler, so the online path rejects a digits-only name while the
     * historical replay path accepts it. The rejection is still recorded in the log.
     */
    public void changeUsername(String name) {
        try {
            router.dispatch(UsernameChanged.of(UserState.AGGREGATE_ID, name));
        } catch (IllegalArgumentException e) {
            router.appendRaw(UsernameChangeRejected.of(UserState.AGGREGATE_ID, name, e.getMessage()));
            throw e;
        }
    }

    /**
     * Email change is a request/response pair sharing one {@code correlationId}. Online, the current
     * policy decides; the decision is stored and later found by correlation during replay.
     */
    public void changeEmail(String email) {
        UUID correlationId = UUID.randomUUID();
        EmailChangeRequested requested = EmailChangeRequested.of(UserState.AGGREGATE_ID, correlationId, email);
        router.appendRaw(requested);
        try {
            emailPolicy.validate(email);
            router.dispatch(EmailChangeResult.accepted(
                    UserState.AGGREGATE_ID, correlationId, requested.id(), email));
        } catch (IllegalArgumentException e) {
            router.appendRaw(EmailChangeResult.rejected(
                    UserState.AGGREGATE_ID, correlationId, requested.id(), email, e.getMessage()));
            throw e;
        }
    }

    /** Writes a username change straight into the log, as if old rules had accepted it. */
    public void seedLegacyUsername(String name) {
        router.appendRaw(UsernameChanged.of(UserState.AGGREGATE_ID, name));
    }

    /** Writes an accepted email change request/response pair straight into the log (old rules). */
    public void seedLegacyEmail(String email) {
        UUID correlationId = UUID.randomUUID();
        EmailChangeRequested requested = EmailChangeRequested.of(UserState.AGGREGATE_ID, correlationId, email);
        router.appendRaw(requested);
        router.appendRaw(EmailChangeResult.accepted(
                UserState.AGGREGATE_ID, correlationId, requested.id(), email));
    }
}
