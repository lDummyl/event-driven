package com.example.eventdriven.state;

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

    public UserStateService(UserStateRepository repository) {
        this.repository = repository;
    }

    /** Mutable working state, used by handlers through the router. */
    public synchronized UserState live() {
        return working;
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
        repository.deleteAll();
        persist();
    }
}
