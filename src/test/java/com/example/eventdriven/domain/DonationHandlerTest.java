package com.example.eventdriven.domain;

import com.example.eventdriven.config.RatingProperties;
import com.example.eventdriven.state.UserState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DonationHandlerTest {

    private final UserState state = UserState.empty();

    @Test
    void usesOldRuleWhenConfigured() {
        handlerWithRate(200).apply(state, DonationReceived.of(100, 999));
        assertThat(state.getPoints()).isEqualTo(200);
    }

    @Test
    void usesNewRuleWhenConfigured() {
        handlerWithRate(50).apply(state, DonationReceived.of(100, 999));
        assertThat(state.getPoints()).isEqualTo(50);
    }

    private DonationHandler handlerWithRate(long rate) {
        RatingProperties properties = new RatingProperties();
        properties.setPointsPer100(rate);
        return new DonationHandler(properties);
    }
}
