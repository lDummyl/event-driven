package com.example.eventdriven.domain;

import com.example.eventdriven.config.RatingProperties;
import com.example.eventdriven.core.EventHandler;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Component;

/**
 * Recomputes donation points from the amount with the current rating rule. The stored
 * {@code pointsAwarded} is intentionally ignored, which is what makes a rule change re-score all
 * history on rebuild.
 */
@Component
public class DonationHandler implements EventHandler<DonationReceived> {

    private final RatingProperties rating;

    public DonationHandler(RatingProperties rating) {
        this.rating = rating;
    }

    @Override
    public Class<DonationReceived> eventType() {
        return DonationReceived.class;
    }

    @Override
    public void apply(UserState state, DonationReceived event) {
        long points = Math.floorDiv(event.amount(), 100L) * rating.getPointsPer100();
        state.setPoints(state.getPoints() + points);
    }
}
