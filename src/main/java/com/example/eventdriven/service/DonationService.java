package com.example.eventdriven.service;

import com.example.eventdriven.config.RatingProperties;
import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.domain.DonationReceived;
import com.example.eventdriven.state.UserState;
import org.springframework.stereotype.Service;

/**
 * Donation operations. Turns a donation command into an event; the {@link EventRouter} (the proxy)
 * decides how it is processed.
 */
@Service
public class DonationService {

    private final EventRouter router;
    private final RatingProperties rating;

    public DonationService(EventRouter router, RatingProperties rating) {
        this.router = router;
        this.rating = rating;
    }

    public void donate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation amount must be positive");
        }
        long points = Math.floorDiv(amount, 100L) * rating.getPointsPer100();
        router.dispatch(DonationReceived.of(UserState.AGGREGATE_ID, amount, points));
    }
}
