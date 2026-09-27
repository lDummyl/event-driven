package com.example.eventdriven.domain;

import com.example.eventdriven.core.EventHandler;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Component;

/**
 * Response half of an email change. Online it applies an accepted change; on rebuild the default
 * {@code replay} does the same (idempotent), while the request event is the one that re-derives the
 * decision from history.
 */
@Component
public class EmailChangeResultHandler implements EventHandler<EmailChangeResult> {

    @Override
    public Class<EmailChangeResult> eventType() {
        return EmailChangeResult.class;
    }

    @Override
    public void apply(UserState state, EmailChangeResult event) {
        if (event.accepted()) {
            state.setEmail(event.email());
        }
    }
}
