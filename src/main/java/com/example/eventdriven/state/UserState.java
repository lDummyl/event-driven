package com.example.eventdriven.state;

/**
 * The projection: what the current user looks like right now. Mutable working copy that handlers
 * update; it is mirrored into H2 by {@link UserStateService}.
 */
public class UserState {

    /** Identity of the single aggregate owned by this projection (until multi-user arrives). */
    public static final String AGGREGATE_ID = "user:1";

    private String username = "anon";
    private String email = "anon@example.com";
    private long points;

    public static UserState empty() {
        return new UserState();
    }

    public UserState copy() {
        UserState copy = new UserState();
        copy.username = username;
        copy.email = email;
        copy.points = points;
        return copy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
