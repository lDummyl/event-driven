package com.example.eventdriven.domain;

import org.springframework.stereotype.Component;

/**
 * Current (new) username rules. The rule "no digits-only names" did not exist before; historical
 * events may violate it and are replayed through {@code EventHandler#replay}, which skips this check.
 */
@Component
public class UsernamePolicy {

    public void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (name.matches("\\d+")) {
            throw new IllegalArgumentException("New rule: a username of digits only is not allowed");
        }
    }
}
