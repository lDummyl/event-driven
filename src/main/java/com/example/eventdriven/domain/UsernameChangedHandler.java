package com.example.eventdriven.domain;

import com.example.eventdriven.core.CorrelationLookup;
import com.example.eventdriven.core.EventHandler;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Component;

@Component
public class UsernameChangedHandler implements EventHandler<UsernameChanged> {

    private final UsernamePolicy policy;

    public UsernameChangedHandler(UsernamePolicy policy) {
        this.policy = policy;
    }

    @Override
    public Class<UsernameChanged> eventType() {
        return UsernameChanged.class;
    }

    /** Online: the current rule is enforced, so a digits-only name cannot be set now. */
    @Override
    public void apply(UserState state, UsernameChanged event) {
        policy.validate(event.newName());
        state.setUsername(event.newName());
    }

    /** Rebuild: the historical fact is applied as-is, bypassing the current rule. */
    @Override
    public void replay(UserState state, UsernameChanged event, CorrelationLookup log) {
        state.setUsername(event.newName());
    }
}
