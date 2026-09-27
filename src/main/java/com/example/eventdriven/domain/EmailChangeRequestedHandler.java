package com.example.eventdriven.domain;

import com.example.eventdriven.core.CorrelationLookup;
import com.example.eventdriven.core.EventHandler;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Component;

/**
 * Request half of an email change. Online it does nothing (the result applies the effect). During a
 * rebuild it finds the recorded result by {@code correlationId} and, if it was accepted, applies the
 * email without re-validating it - the past already decided.
 */
@Component
public class EmailChangeRequestedHandler implements EventHandler<EmailChangeRequested> {

    @Override
    public Class<EmailChangeRequested> eventType() {
        return EmailChangeRequested.class;
    }

    @Override
    public void apply(UserState state, EmailChangeRequested event) {
        // online: wait for the result event
    }

    @Override
    public void replay(UserState state, EmailChangeRequested event, CorrelationLookup log) {
        log.findResponse(event.correlationId(), EmailChangeResult.class)
                .filter(EmailChangeResult::accepted)
                .ifPresent(result -> state.setEmail(result.email()));
    }
}
