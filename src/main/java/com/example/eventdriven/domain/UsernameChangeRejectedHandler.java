package com.example.eventdriven.domain;

import com.example.eventdriven.core.EventHandler;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Component;

/** Rejections are part of history but never change the state. */
@Component
public class UsernameChangeRejectedHandler implements EventHandler<UsernameChangeRejected> {

    @Override
    public Class<UsernameChangeRejected> eventType() {
        return UsernameChangeRejected.class;
    }

    @Override
    public void apply(UserState state, UsernameChangeRejected event) {
        // no-op: a rejected change has no effect
    }
}
