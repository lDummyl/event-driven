package com.example.eventdriven.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The current rating rule. Changing {@code app.rating.points-per-100} changes how many points a
 * donation is worth, and because donations are {@code RECALCULABLE} the whole history is re-scored.
 */
@ConfigurationProperties(prefix = "app.rating")
public class RatingProperties {

    /** Points granted per 100 units donated. Old rule: 200. New rule: 50. */
    private long pointsPer100 = 200;

    public long getPointsPer100() {
        return pointsPer100;
    }

    public void setPointsPer100(long pointsPer100) {
        this.pointsPer100 = pointsPer100;
    }
}
