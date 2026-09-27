package com.example.eventdriven.domain;

import org.springframework.stereotype.Component;

/** Current (new) email rules. The banned {@code legacy.mail} domain is a new restriction. */
@Component
public class EmailPolicy {

    public void validate(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
        if (email.endsWith("@legacy.mail")) {
            throw new IllegalArgumentException("New rule: the legacy.mail domain is no longer allowed");
        }
    }
}
