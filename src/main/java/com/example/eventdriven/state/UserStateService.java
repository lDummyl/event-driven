package com.example.eventdriven.state;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Holds the live working copy of the state and mirrors it into the in-memory H2 database.
 * The DB projection is recreated from the log, so it is disposable by design.
 */
@Service
public class UserStateService {

    private static final Long SINGLE_USER = 1L;

    private final UserStateRepository repository;

    private UserState working = UserState.empty();
    private final Set<UUID> appliedEventIds = new HashSet<>();

    public UserStateService(UserStateRepository repository) {
        this.repository = repository;
    }

    /** Mutable working state, used by handlers through the router. */
    public synchronized UserState live() {
        return working;
    }

    /**
     * Applies an event effect exactly once. The idempotency seam that protects the projection from
     * duplicate delivery once it is fed by a live stream; on a full rebuild it is inert because the
     * applied set is cleared first.
     */
    public synchronized void applyIfNew(UUID eventId, Runnable action) {
        if (!appliedEventIds.add(eventId)) {
            return;
        }
        action.run();
    }

    /** A defensive copy backed by the DB projection (falls back to memory before first persist). */
    public synchronized UserState get() {
        return repository.findById(SINGLE_USER)
                .map(entity -> {
                    UserState state = new UserState();
                    state.setUsername(entity.getUsername());
                    state.setEmail(entity.getEmail());
                    state.setPoints(entity.getPoints());
                    return state;
                })
                .orElseGet(working::copy);
    }

    public synchronized void persist() {
        UserStateEntity entity = new UserStateEntity();
        entity.setId(SINGLE_USER);
        entity.setUsername(working.getUsername());
        entity.setEmail(working.getEmail());
        entity.setPoints(working.getPoints());
        repository.save(entity);
    }

    public synchronized void reset() {
        working = UserState.empty();
        appliedEventIds.clear();
        repository.deleteAll();
        persist();
    }
}
